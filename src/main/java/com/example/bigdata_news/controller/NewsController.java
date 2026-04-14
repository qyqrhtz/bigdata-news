package com.example.bigdata_news.controller;

import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.service.CozeService;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社交媒体热点新闻分析控制器
 * 优化重点：支持 20w 数据的高性能分析与语义检索
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class NewsController {

    private final NewsService newsService;
    private final CozeService cozeService;

    public NewsController(NewsService newsService,CozeService cozeService) {
        this.newsService = newsService;
        this.cozeService = cozeService;
    }
    @PostMapping("/rag/chat")
    public Map<String, Object> ragChat(@RequestBody Map<String, String> request) {
        String question = request.get("question");

        // 1. 调已有语义搜索
        List<Map<String, Object>> results = newsService.semanticSearch(question, 3);

        // 2. 拼接context
        StringBuilder contextBuilder = new StringBuilder();
        for (Map<String, Object> item : results) {
            contextBuilder.append(item.get("title")).append("\n");
            contextBuilder.append(item.get("content")).append("\n\n");
        }
        String context = contextBuilder.toString();

        // 3. 调 Coze（你后面实现）
        String answer = cozeService.callCoze(question, context);

        return Map.of(
                "question", question,
                "answer", answer,
                "sources", results
        );
    }
    /**
     * [RAG 接口] 语义搜索：通过调用 Python Flask 接口实现基于向量的精准检索
     */
    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String query,
                                            @RequestParam(defaultValue = "10") int topK) {
        return newsService.semanticSearch(query, topK);
    }

    /**
     * 【大屏核心】获取全局统计数据
     */
    @GetMapping("/stats/dashboard")
    public Map<String, Object> getDashboardStats() {
        return newsService.getGlobalDashboardStats();
    }

    /**
     * 分页获取原始新闻列表
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