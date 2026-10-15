package com.example.bigdata_news.controller;

import com.example.bigdata_news.dto.RagChatResponse;
import com.example.bigdata_news.dto.RagChatRequest;
import com.example.bigdata_news.dto.RagSearchResult;
import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.service.CozeService;
import com.example.bigdata_news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Tag(name = "News APIs", description = "News pagination, dashboard statistics, semantic search and RAG chat")
public class NewsController {

    private final NewsService newsService;
    private final CozeService cozeService;

    public NewsController(NewsService newsService,CozeService cozeService) {
        this.newsService = newsService;
        this.cozeService = cozeService;
    }

    @Operation(summary = "Ask a RAG question", description = "Retrieve related news through FAISS and call the configured Coze workflow to generate an answer.")
    @PostMapping("/rag/chat")
    public RagChatResponse ragChat(@Valid @RequestBody RagChatRequest request) {
        String question = request.question();

        // 1. 调已有语义搜索
        List<RagSearchResult> results = newsService.semanticSearch(question, 3);

        // 2. 拼接context
        StringBuilder contextBuilder = new StringBuilder();
        for (RagSearchResult item : results) {
            contextBuilder.append(item.title()).append("\n");
            contextBuilder.append(item.url()).append("\n\n");
        }
        String context = contextBuilder.toString();

        // 3. 调 Coze（你后面实现）
        String answer = cozeService.callCoze(question, context);

        return new RagChatResponse(question, answer, results);
    }
    /**
     * [RAG 接口] 语义搜索：通过调用 Python Flask 接口实现基于向量的精准检索
     */
    @Operation(summary = "Semantic news search", description = "Search top-k semantically similar news titles from the FAISS RAG service.")
    @GetMapping("/search")
    public List<RagSearchResult> search(@RequestParam String query,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(20) int topK) {
        return newsService.semanticSearch(query, topK);
    }

    /**
     * 【大屏核心】获取全局统计数据
     */
    @Operation(summary = "Dashboard statistics", description = "Get aggregated category, year trend, author and density statistics for the dashboard.")
    @GetMapping("/stats/dashboard")
    public Map<String, Object> getDashboardStats() {
        return newsService.getGlobalDashboardStats();
    }

    /**
     * 分页获取原始新闻列表
     */
    @Operation(summary = "Paged news list")
    @GetMapping("/page")
    public Page<News> getNewsPage(
            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size, limited to 1-100")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return newsService.findPage(page, size);
    }

    @Operation(summary = "Get news by id")
    @GetMapping("/{id}")
    public News getNewsById(@PathVariable String id) {
        return newsService.findById(id);
    }

    @Operation(summary = "Delete news by id")
    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteById(id);
    }
}
