package com.example.bigdata_news.serviceimpl;

import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.repository.NewsRepository;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    @Override
    public News findById(String uniquekey) {
        return newsRepository.findById(uniquekey).orElse(null);
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteById(String uniquekey) {
        newsRepository.deleteById(uniquekey);
    }

    @Override
    public Page<News> findPage(int page, int size) {
        return newsRepository.findAll(PageRequest.of(page, size));
    }

    // --- 实现接口要求的统计方法 ---

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        return newsRepository.countByCategory();
    }

    @Override
    public List<Map<String, Object>> getYearTrends() {
        return newsRepository.countByYear();
    }

    @Override
    public List<Map<String, Object>> getAuthorStats() {
        return newsRepository.findTopAuthors();
    }

    @Override
    public List<Map<String, Object>> getCrossDomainAuthorStats() {
        return newsRepository.findCrossDomainAuthors();
    }

    @Override
    public List<Map<String, Object>> getClickbaitIndexStats() {
        return newsRepository.findAvgHeadlineLengthByCategory();
    }

    @Override
    public List<Map<String, Object>> getDescriptionDensityStats() {
        return newsRepository.countByDescriptionDensity();
    }

    @Override
    public Map<String, Object> getGlobalDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("categories", getCategoryStats());
        stats.put("yearTrends", getYearTrends());
        stats.put("authors", getAuthorStats());
        stats.put("botDetection", getCrossDomainAuthorStats());
        stats.put("clickbaitIndex", getClickbaitIndexStats());
        stats.put("density", getDescriptionDensityStats());
        return stats;
    }
}