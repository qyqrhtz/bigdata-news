package com.example.bigdata_news.controller;

import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 社交媒体热点新闻分析控制器
 * 优化重点：支持 20w 数据的高性能分析接口
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 【核心】获取具有研究价值的全局统计数据
     * 包含：标题党指数、跨领域作者识别、信息密度等
     */
    @GetMapping("/stats/dashboard")
    public Map<String, Object> getDashboardStats() {
        return newsService.getGlobalDashboardStats();
    }

    /**
     * 分页获取原始新闻列表 (用于下方表格展示)
     */
    @GetMapping("/page")
    public Page<News> getNewsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return newsService.findPage(page, size);
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable String id) {
        return newsService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteById(id);
    }
}