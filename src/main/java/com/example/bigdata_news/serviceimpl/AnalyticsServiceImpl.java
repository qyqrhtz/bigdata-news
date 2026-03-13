package com.example.bigdata_news.serviceimpl;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import com.example.bigdata_news.entity.AnalyticsResult;
import com.example.bigdata_news.repository.AnalyticsResultRepository;
import com.example.bigdata_news.repository.NewsRepository;
import com.example.bigdata_news.service.AnalyticsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final String KEY_TOPICS = "topics_v1";
    private static final String KEY_AUTHOR_ANOMALY = "author_anomaly_v1";
    private static final String KEY_DUPLICATES = "duplicates_v1";
    private static final String KEY_BURSTS = "bursts_v1";

    private final NewsRepository newsRepository;
    private final AnalyticsResultRepository analyticsResultRepository;
    private final ObjectMapper objectMapper;

    @Value("${analytics.lda.num-topics:12}")
    private int numTopics;

    @Value("${analytics.lda.num-iterations:600}")
    private int numIterations;

    @Value("${analytics.lda.num-top-words:12}")
    private int numTopWords;

    @Value("classpath:stopwords-en.txt")
    private Resource stopwordsResource;

    public AnalyticsServiceImpl(
            NewsRepository newsRepository,
            AnalyticsResultRepository analyticsResultRepository,
            ObjectMapper objectMapper
    ) {
        this.newsRepository = newsRepository;
        this.analyticsResultRepository = analyticsResultRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, Object> getTopics() {
        return readJson(KEY_TOPICS);
    }

    @Override
    public Map<String, Object> getAuthorAnomalies() {
        return readJson(KEY_AUTHOR_ANOMALY);
    }

    @Override
    public Map<String, Object> getDuplicates() {
        return readJson(KEY_DUPLICATES);
    }

    @Override
    public Map<String, Object> getBursts() {
        return readJson(KEY_BURSTS);
    }

    @Override
    @Transactional//事务管理注解
    public Map<String, Object> recomputeAll() {
        List<NewsRepository.NewsSlice> rows = newsRepository.findAllSlices();
        Map<String, Object> topics = computeTopics(rows);
        Map<String, Object> authorAnomaly = computeAuthorAnomaly(rows);
        Map<String, Object> duplicates = computeDuplicates(rows);
        Map<String, Object> bursts = computeBursts(rows, topics);

        writeJson(KEY_TOPICS, topics);
        writeJson(KEY_AUTHOR_ANOMALY, authorAnomaly);
        writeJson(KEY_DUPLICATES, duplicates);
        writeJson(KEY_BURSTS, bursts);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("updatedAt", LocalDateTime.now().toString());
        result.put("keys", List.of(KEY_TOPICS, KEY_AUTHOR_ANOMALY, KEY_DUPLICATES, KEY_BURSTS));
        return result;
    }

    private Map<String, Object> computeTopics(List<NewsRepository.NewsSlice> rows) {
        List<NewsRepository.NewsSlice> docs = rows.stream()//转换成数据流
                .filter(r -> r.getTitle() != null && !r.getTitle().isBlank())
                .collect(Collectors.toList());

        List<String> titles = docs.stream().map(NewsRepository.NewsSlice::getTitle).toList();

        List<Pipe> pipeList = new ArrayList<>();
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[a-zA-Z]{2,}")));

        TokenSequenceRemoveStopwords stopwords = new TokenSequenceRemoveStopwords(false, false);
        try {
            List<String> words = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stopwordsResource.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String w = line.trim();
                    if (!w.isEmpty() && !w.startsWith("#")) words.add(w);
                }
            }
            if (!words.isEmpty()) {
                stopwords.addStopWords(words.toArray(new String[0]));
            }
        } catch (Exception e) {
            // fall back: no stopwords loaded
        }
        pipeList.add(stopwords);
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        instances.addThruPipe(new StringArrayIterator(titles.toArray(new String[0])));

        ParallelTopicModel model;
        try {
            model = new ParallelTopicModel(numTopics, 1.0, 0.01);
            model.addInstances(instances);
            model.setNumThreads(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
            model.setNumIterations(numIterations);
            model.estimate();
        } catch (Exception e) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", "LDA training failed: " + e.getMessage());
            return err;
        }

        // topic -> top words
        List<Map<String, Object>> topicWords = new ArrayList<>();
        ArrayList<TreeSet<IDSorter>> sortedWords = model.getSortedWords();
        for (int topic = 0; topic < numTopics; topic++) {
            List<Map<String, Object>> words = new ArrayList<>();
            int rank = 0;
            for (IDSorter idCountPair : sortedWords.get(topic)) {
                String word = (String) model.getAlphabet().lookupObject(idCountPair.getID());
                Map<String, Object> w = new LinkedHashMap<>();
                w.put("word", word);
                w.put("weight", idCountPair.getWeight());
                words.add(w);
                if (++rank >= numTopWords) break;
            }
            Map<String, Object> t = new LinkedHashMap<>();
            t.put("topic", topic);
            t.put("words", words);
            topicWords.add(t);
        }

        DateTimeFormatter ym = DateTimeFormatter.ofPattern("yyyy-MM");
        Set<String> periodSet = new TreeSet<>();
        Map<String, long[]> periodTopicCounts = new HashMap<>();
        Map<Integer, Map<String, Integer>> topicCategory = new HashMap<>();

        for (int i = 0; i < instances.size(); i++) {
            double[] probs = model.getTopicProbabilities(i);
            int dominant = argMax(probs);

            LocalDateTime dt = docs.get(i).getDate();
            String period = (dt == null) ? "unknown" : dt.format(ym);
            periodSet.add(period);
            periodTopicCounts.computeIfAbsent(period, p -> new long[numTopics])[dominant]++;

            String category = docs.get(i).getCategory();
            if (category == null || category.isBlank()) category = "UNKNOWN";
            topicCategory.computeIfAbsent(dominant, k -> new HashMap<>())
                    .merge(category, 1, Integer::sum);
        }

        List<String> periods = new ArrayList<>(periodSet);
        List<Map<String, Object>> series = new ArrayList<>();
        for (int topic = 0; topic < numTopics; topic++) {
            List<Long> data = new ArrayList<>();
            for (String p : periods) {
                long[] arr = periodTopicCounts.getOrDefault(p, new long[numTopics]);
                data.add(arr[topic]);
            }
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("name", "Topic " + topic);
            s.put("topic", topic);
            s.put("data", data);
            series.add(s);
        }

        List<Map<String, Object>> heatmap = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Integer>> e : topicCategory.entrySet()) {
            int topic = e.getKey();
            for (Map.Entry<String, Integer> c : e.getValue().entrySet()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("topic", topic);
                item.put("category", c.getKey());
                item.put("value", c.getValue());
                heatmap.add(item);
            }
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("updatedAt", LocalDateTime.now().toString());
        out.put("numTopics", numTopics);
        out.put("topicWords", topicWords);
        out.put("timeSeries", Map.of("periods", periods, "series", series));
        out.put("topicCategoryHeatmap", heatmap);
        return out;
    }

    private Map<String, Object> computeAuthorAnomaly(List<NewsRepository.NewsSlice> rows) {
        record AuthorAgg(long posts, Set<String> categories, List<Integer> titleLens, Map<String, Long> domainCounts, Map<String, Long> periodCounts) {}

        DateTimeFormatter ym = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, AuthorAgg> agg = new HashMap<>();

        for (NewsRepository.NewsSlice r : rows) {
            String author = (r.getAuthorName() == null || r.getAuthorName().isBlank()) ? "UNKNOWN" : r.getAuthorName().trim();
            AuthorAgg a = agg.get(author);
            if (a == null) {
                a = new AuthorAgg(0, new HashSet<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>());
                agg.put(author, a);
            }

            long posts = a.posts + 1;
            if (r.getCategory() != null && !r.getCategory().isBlank()) a.categories.add(r.getCategory());
            if (r.getTitle() != null) a.titleLens.add(r.getTitle().length());

            String domain = extractDomain(r.getUrl());
            if (domain != null) a.domainCounts.merge(domain, 1L, Long::sum);

            String period = (r.getDate() == null) ? "unknown" : r.getDate().format(ym);
            a.periodCounts.merge(period, 1L, Long::sum);

            agg.put(author, new AuthorAgg(posts, a.categories, a.titleLens, a.domainCounts, a.periodCounts));
        }

        // Build feature table
        record AuthorFeat(String author, long posts, int categoriesCovered, double titleLenMean, double titleLenStd,
                          double domainConcentration, double timeEntropy) {}

        List<AuthorFeat> feats = new ArrayList<>();
        for (Map.Entry<String, AuthorAgg> e : agg.entrySet()) {
            String author = e.getKey();
            AuthorAgg a = e.getValue();
            if (a.posts < 5) continue; // ignore tiny samples for stability

            int cat = a.categories.size();
            double mean = mean(a.titleLens);
            double std = std(a.titleLens, mean);
            double domConc = concentration(a.domainCounts);
            double ent = entropy(a.periodCounts);

            feats.add(new AuthorFeat(author, a.posts, cat, mean, std, domConc, ent));
        }

        if (feats.isEmpty()) {
            return Map.of("updatedAt", LocalDateTime.now().toString(), "items", List.of());
        }

        // z-score normalize selected "anomaly-ish" dimensions
        double[] postsArr = feats.stream().mapToDouble(f -> f.posts).toArray();
        double[] catArr = feats.stream().mapToDouble(f -> f.categoriesCovered).toArray();
        double[] domArr = feats.stream().mapToDouble(f -> f.domainConcentration).toArray();
        double[] entArr = feats.stream().mapToDouble(f -> f.timeEntropy).toArray();
        double[] stdArr = feats.stream().mapToDouble(f -> f.titleLenStd).toArray();

        Norm nPosts = norm(postsArr);
        Norm nCat = norm(catArr);
        Norm nDom = norm(domArr);
        Norm nEnt = norm(entArr);
        Norm nStd = norm(stdArr);

        // score: higher is more "anomalous"
        // - posts high, categoriesCovered high, domainConcentration high, titleLenStd high, timeEntropy low (bursty) => anomalous
        List<Map<String, Object>> items = new ArrayList<>();
        for (AuthorFeat f : feats) {
            double zPosts = z(f.posts, nPosts);
            double zCat = z(f.categoriesCovered, nCat);
            double zDom = z(f.domainConcentration, nDom);
            double zStd = z(f.titleLenStd, nStd);
            double zEnt = z(f.timeEntropy, nEnt);

            double raw = 0.30 * zCat + 0.25 * zPosts + 0.25 * zDom + 0.15 * zStd + 0.20 * (-zEnt);
            double score = sigmoid(raw) * 100.0;

            Map<String, Double> contributions = new LinkedHashMap<>();
            contributions.put("categoriesCovered", 0.30 * zCat);
            contributions.put("posts", 0.25 * zPosts);
            contributions.put("domainConcentration", 0.25 * zDom);
            contributions.put("titleLenStd", 0.15 * zStd);
            contributions.put("timeEntropyLow", 0.20 * (-zEnt));

            List<Map<String, Object>> topReasons = contributions.entrySet().stream()
                    .sorted((a, b) -> Double.compare(Math.abs(b.getValue()), Math.abs(a.getValue())))
                    .limit(3)
                    .map(en -> Map.<String, Object>of("feature", en.getKey(), "contribution", en.getValue()))
                    .toList();

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("author", f.author);
            item.put("score", score);
            item.put("posts", f.posts);
            item.put("categoriesCovered", f.categoriesCovered);
            item.put("domainConcentration", f.domainConcentration);
            item.put("timeEntropy", f.timeEntropy);
            item.put("titleLenMean", f.titleLenMean);
            item.put("titleLenStd", f.titleLenStd);
            item.put("reasons", topReasons);
            items.add(item);
        }

        items.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("updatedAt", LocalDateTime.now().toString());
        out.put("items", items.stream().limit(50).toList());
        return out;
    }

    private Map<String, Object> computeDuplicates(List<NewsRepository.NewsSlice> rows) {
        // SimHash + simple LSH bucketing (4 bands of 16 bits) to avoid O(N^2)
        List<NewsRepository.NewsSlice> docs = rows.stream()
                .filter(r -> r.getTitle() != null && !r.getTitle().isBlank())
                .collect(Collectors.toList());

        int n = docs.size();
        long[] hashes = new long[n];
        for (int i = 0; i < n; i++) {
            hashes[i] = simHash64(docs.get(i).getTitle());
        }

        Map<Long, List<Integer>> buckets = new HashMap<>(n * 2);
        for (int i = 0; i < n; i++) {
            long h = hashes[i];
            for (int band = 0; band < 4; band++) {
                long key = bandKey(h, band);
                buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
            }
        }

        UnionFind uf = new UnionFind(n);
        int compareLimitPerBucket = 200; // cap to keep worst-case buckets from exploding
        int maxHamming = 3;
        AtomicInteger comparedPairs = new AtomicInteger(0);

        for (List<Integer> idxs : buckets.values()) {
            if (idxs.size() < 2) continue;
            if (idxs.size() > compareLimitPerBucket) {
                // sample to avoid quadratic blowups on huge buckets
                Collections.shuffle(idxs, new Random(42));
                idxs = idxs.subList(0, compareLimitPerBucket);
            }
            for (int a = 0; a < idxs.size(); a++) {
                int i = idxs.get(a);
                for (int b = a + 1; b < idxs.size(); b++) {
                    int j = idxs.get(b);
                    comparedPairs.incrementAndGet();
                    if (hamming(hashes[i], hashes[j]) <= maxHamming) {
                        uf.union(i, j);
                    }
                }
            }
        }

        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        List<List<Integer>> clusters = groups.values().stream()
                .filter(g -> g.size() >= 2)
                .sorted((a, b) -> Integer.compare(b.size(), a.size()))
                .toList();

        long dupDocs = clusters.stream().mapToLong(List::size).sum();
        double duplicateRate = n == 0 ? 0.0 : (double) dupDocs / (double) n;

        List<Map<String, Object>> topClusters = new ArrayList<>();
        int clusterLimit = 30;
        int sampleTitles = 6;
        for (int c = 0; c < Math.min(clusterLimit, clusters.size()); c++) {
            List<Integer> g = clusters.get(c);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("size", g.size());
            List<Map<String, Object>> samples = new ArrayList<>();
            for (int s = 0; s < Math.min(sampleTitles, g.size()); s++) {
                NewsRepository.NewsSlice r = docs.get(g.get(s));
                Map<String, Object> sm = new LinkedHashMap<>();
                sm.put("title", r.getTitle());
                sm.put("date", r.getDate() == null ? null : r.getDate().toString());
                sm.put("category", r.getCategory());
                sm.put("author", r.getAuthorName());
                sm.put("url", r.getUrl());
                samples.add(sm);
            }
            item.put("samples", samples);
            topClusters.add(item);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("updatedAt", LocalDateTime.now().toString());
        out.put("documents", n);
        out.put("duplicateDocuments", dupDocs);
        out.put("duplicateRate", duplicateRate);
        out.put("clusters", clusters.size());
        out.put("comparedPairs", comparedPairs.get());
        out.put("topClusters", topClusters);
        out.put("note", "Near-duplicate by SimHash(title) with LSH bucketing; tune maxHamming if needed.");
        return out;
    }

    private Map<String, Object> computeBursts(List<NewsRepository.NewsSlice> rows, Map<String, Object> topicsResult) {
        // Category-month bursts via rolling z-score
        DateTimeFormatter ym = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, Map<String, Long>> categoryPeriodCounts = new HashMap<>();
        Set<String> periodSet = new TreeSet<>();

        for (NewsRepository.NewsSlice r : rows) {
            if (r.getDate() == null) continue;
            String period = r.getDate().format(ym);
            periodSet.add(period);
            String category = (r.getCategory() == null || r.getCategory().isBlank()) ? "UNKNOWN" : r.getCategory();
            categoryPeriodCounts.computeIfAbsent(category, k -> new HashMap<>())
                    .merge(period, 1L, Long::sum);
        }

        List<String> periods = new ArrayList<>(periodSet);
        int window = 6;
        List<Map<String, Object>> categoryBursts = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> e : categoryPeriodCounts.entrySet()) {
            String category = e.getKey();
            long[] series = new long[periods.size()];
            for (int i = 0; i < periods.size(); i++) {
                series[i] = e.getValue().getOrDefault(periods.get(i), 0L);
            }
            categoryBursts.addAll(rollingZBursts(category, periods, series, window, 3.0));
        }
        categoryBursts.sort((a, b) -> Double.compare((Double) b.get("z"), (Double) a.get("z")));

        // Topic bursts from topicsResult if present (uses already computed time series)
        List<Map<String, Object>> topicBursts = new ArrayList<>();
        Object tsObj = topicsResult.get("timeSeries");
        if (tsObj instanceof Map<?, ?> ts) {
            Object ps = ts.get("periods");
            Object ss = ts.get("series");
            if (ps instanceof List<?> pList && ss instanceof List<?> sList) {
                List<String> p = pList.stream().map(String::valueOf).toList();
                for (Object o : sList) {
                    if (!(o instanceof Map<?, ?> sm)) continue;
                    Object name = sm.get("name");
                    Object data = sm.get("data");
                    Object topic = sm.get("topic");
                    if (!(data instanceof List<?> dl)) continue;
                    long[] arr = new long[dl.size()];
                    for (int i = 0; i < dl.size(); i++) arr[i] = Long.parseLong(String.valueOf(dl.get(i)));
                    String label = (topic == null) ? String.valueOf(name) : ("Topic " + topic);
                    topicBursts.addAll(rollingZBursts(label, p, arr, window, 3.0));
                }
            }
        }
        topicBursts.sort((a, b) -> Double.compare((Double) b.get("z"), (Double) a.get("z")));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("updatedAt", LocalDateTime.now().toString());
        out.put("windowMonths", window);
        out.put("thresholdZ", 3.0);
        out.put("categoryBursts", categoryBursts.stream().limit(50).toList());
        out.put("topicBursts", topicBursts.stream().limit(50).toList());
        return out;
    }

    private static List<Map<String, Object>> rollingZBursts(String name, List<String> periods, long[] series, int window, double threshold) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (int i = 0; i < series.length; i++) {
            int from = Math.max(0, i - window);
            int to = i; // exclude current
            int len = to - from;
            if (len < 3) continue;
            double mean = 0;
            for (int k = from; k < to; k++) mean += series[k];
            mean /= len;
            double var = 0;
            for (int k = from; k < to; k++) {
                double d = series[k] - mean;
                var += d * d;
            }
            double std = Math.sqrt(var / Math.max(1, len - 1));
            if (std == 0) continue;
            double z = (series[i] - mean) / std;
            if (z >= threshold) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", name);
                m.put("period", periods.get(i));
                m.put("count", series[i]);
                m.put("baselineMean", mean);
                m.put("baselineStd", std);
                m.put("z", z);
                out.add(m);
            }
        }
        return out;
    }

    private static long bandKey(long hash, int band) {
        int shift = band * 16;
        long bandBits = (hash >>> shift) & 0xFFFFL;
        return (((long) band) << 48) | bandBits; // include band id to separate bands
    }

    private static int hamming(long a, long b) {
        return Long.bitCount(a ^ b);
    }

    private static long simHash64(String text) {
        // Tokenize to words (a-z) and weight by term frequency in the title
        String norm = text.toLowerCase(Locale.ROOT);
        String[] tokens = norm.split("[^a-z]+");
        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) {
            if (t == null || t.length() < 2) continue;
            tf.merge(t, 1, Integer::sum);
        }
        int[] v = new int[64];
        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            long h = fnv1a64(e.getKey());
            int w = e.getValue();
            for (int i = 0; i < 64; i++) {
                boolean bit = ((h >>> i) & 1L) == 1L;
                v[i] += bit ? w : -w;
            }
        }
        long out = 0L;
        for (int i = 0; i < 64; i++) {
            if (v[i] >= 0) out |= (1L << i);
        }
        return out;
    }

    private static long fnv1a64(String s) {
        long h = 0xcbf29ce484222325L;
        for (int i = 0; i < s.length(); i++) {
            h ^= s.charAt(i);
            h *= 0x100000001b3L;
        }
        return h;
    }

    private static final class UnionFind {
        private final int[] p;
        private final byte[] r;

        UnionFind(int n) {
            p = new int[n];
            r = new byte[n];
            for (int i = 0; i < n; i++) p[i] = i;
        }

        int find(int x) {
            int root = x;
            while (p[root] != root) root = p[root];
            while (p[x] != x) {
                int nx = p[x];
                p[x] = root;
                x = nx;
            }
            return root;
        }

        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (r[ra] < r[rb]) {
                p[ra] = rb;
            } else if (r[ra] > r[rb]) {
                p[rb] = ra;
            } else {
                p[rb] = ra;
                r[ra]++;
            }
        }
    }

    private Map<String, Object> readJson(String key) {
        return analyticsResultRepository.findById(key)
                .map(r -> {
                    try {
                        return objectMapper.readValue(r.getJson(), new TypeReference<Map<String, Object>>() {});
                    } catch (Exception e) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("error", "Failed to parse cached JSON for " + key + ": " + e.getMessage());
                        return m;
                    }
                })
                .orElseGet(() -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("error", "No cached analytics yet");
                    m.put("hint", "POST /api/analytics/recompute to generate");
                    return m;
                });
    }

    private void writeJson(String key, Map<String, Object> obj) {
        String json;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            json = "{\"error\":\"Failed to serialize JSON: " + escape(e.getMessage()) + "\"}";
        }
        AnalyticsResult r = new AnalyticsResult(key, LocalDateTime.now(), json);
        analyticsResultRepository.save(r);
    }

    private static int argMax(double[] arr) {
        int best = 0;
        double bestVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > bestVal) {
                bestVal = arr[i];
                best = i;
            }
        }
        return best;
    }

    private static String extractDomain(String url) {
        if (url == null || url.isBlank()) return null;
        try {
            URI uri = URI.create(url.trim());
            String host = uri.getHost();
            if (host == null) return null;
            host = host.toLowerCase(Locale.ROOT);
            if (host.startsWith("www.")) host = host.substring(4);
            return host;
        } catch (Exception e) {
            return null;
        }
    }

    private static double mean(List<Integer> xs) {
        if (xs == null || xs.isEmpty()) return 0.0;
        double sum = 0;
        for (int x : xs) sum += x;
        return sum / xs.size();
    }

    private static double std(List<Integer> xs, double mean) {
        if (xs == null || xs.size() < 2) return 0.0;
        double ss = 0;
        for (int x : xs) {
            double d = x - mean;
            ss += d * d;
        }
        return Math.sqrt(ss / (xs.size() - 1));
    }

    private static double concentration(Map<String, Long> counts) {
        if (counts == null || counts.isEmpty()) return 0.0;
        long total = counts.values().stream().mapToLong(v -> v).sum();
        if (total == 0) return 0.0;
        long max = counts.values().stream().mapToLong(v -> v).max().orElse(0);
        return (double) max / (double) total; // 1.0 means all from one domain
    }

    private static double entropy(Map<String, Long> counts) {
        if (counts == null || counts.isEmpty()) return 0.0;
        double total = counts.values().stream().mapToDouble(v -> v).sum();
        if (total <= 0) return 0.0;
        double h = 0.0;
        for (long v : counts.values()) {
            if (v <= 0) continue;
            double p = v / total;
            h += -p * Math.log(p);
        }
        return h;
    }

    private record Norm(double mean, double std) {}

    private static Norm norm(double[] xs) {
        double mean = Arrays.stream(xs).average().orElse(0.0);
        double var = 0.0;
        for (double x : xs) {
            double d = x - mean;
            var += d * d;
        }
        double std = xs.length <= 1 ? 1.0 : Math.sqrt(var / (xs.length - 1));
        if (std == 0.0) std = 1.0;
        return new Norm(mean, std);
    }

    private static double z(double x, Norm n) {
        return (x - n.mean) / n.std;
    }

    private static double sigmoid(double x) {
        if (x >= 0) {
            double z = Math.exp(-x);
            return 1.0 / (1.0 + z);
        } else {
            double z = Math.exp(x);
            return z / (1.0 + z);
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

