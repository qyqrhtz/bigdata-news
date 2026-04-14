package com.example.bigdata_news.serviceimpl;

import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.repository.NewsRepository;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);
    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;

    public NewsServiceImpl(NewsRepository newsRepository, RestTemplate restTemplate) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Map<String, Object>> semanticSearch(String query, int topK) {
        String url = "http://127.0.0.1:5000/rag?query=" + query + "&top_k=" + topK;
        try {
            // 注意：这里返回 List.class 是可以的，但建议在 Python 端统一 JSON 格式
            return restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            log.error("调用 Python RAG 服务连接失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> getGlobalDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        // 这里的 key 建议和前端 ECharts 接收的 key 保持一致
        stats.put("categories", newsRepository.countByCategory());
        stats.put("yearTrends", newsRepository.countByYear());
        stats.put("authors", newsRepository.findTopAuthors());
        stats.put("botDetection", newsRepository.findCrossDomainAuthors());
        stats.put("clickbaitIndex", newsRepository.findAvgHeadlineLengthByCategory());
        stats.put("density", newsRepository.countByDescriptionDensity());
        return stats;
    }

    // --- 基础业务逻辑 ---
    @Override public List<News> findAll() { return newsRepository.findAll(); }
    @Override public News findById(String id) { return newsRepository.findById(id).orElse(null); }
    @Override public News save(News news) { return newsRepository.save(news); }
    @Override public void deleteById(String id) { newsRepository.deleteById(id); }
    @Override public Page<News> findPage(int page, int size) {
        return newsRepository.findAll(PageRequest.of(page, size));
    }

    // --- 接口中其他方法的存根实现 (调用 Repository 即可) ---
    @Override public List<Map<String, Object>> getCategoryStats() { return newsRepository.countByCategory(); }
    @Override public List<Map<String, Object>> getYearTrends() { return newsRepository.countByYear(); }
    @Override public List<Map<String, Object>> getAuthorStats() { return newsRepository.findTopAuthors(); }
    @Override public List<Map<String, Object>> getCrossDomainAuthorStats() { return newsRepository.findCrossDomainAuthors(); }
    @Override public List<Map<String, Object>> getClickbaitIndexStats() { return newsRepository.findAvgHeadlineLengthByCategory(); }
    @Override public List<Map<String, Object>> getDescriptionDensityStats() { return newsRepository.countByDescriptionDensity(); }
}