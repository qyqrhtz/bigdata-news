package com.example.bigdata_news.controller;

import com.example.bigdata_news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics APIs", description = "Dashboard aggregation data for visualization pages")
public class StatisticsController {

    private final NewsService newsService;

    public StatisticsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 获取可视化大屏所需的所有统计数据
     * 前端只需请求一次，即可渲染多个 ECharts 图表
     */
    @Operation(summary = "Dashboard statistics", description = "Get all chart-ready dashboard statistics in one request.")
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        return newsService.getGlobalDashboardStats();
    }
}
