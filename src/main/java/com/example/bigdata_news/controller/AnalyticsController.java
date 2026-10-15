package com.example.bigdata_news.controller;

import com.example.bigdata_news.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics APIs", description = "Cached advanced analytics results generated from news data")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "Topic modeling result")
    @GetMapping("/topics")
    public Map<String, Object> topics() {
        return analyticsService.getTopics();
    }

    @Operation(summary = "Author anomaly analysis")
    @GetMapping("/authors/anomaly")
    public Map<String, Object> authorAnomaly() {
        return analyticsService.getAuthorAnomalies();
    }

    @Operation(summary = "Duplicate news analysis")
    @GetMapping("/duplicates")
    public Map<String, Object> duplicates() {
        return analyticsService.getDuplicates();
    }

    @Operation(summary = "Burst topic analysis")
    @GetMapping("/bursts")
    public Map<String, Object> bursts() {
        return analyticsService.getBursts();
    }

    @Operation(summary = "Recompute all analytics")
    @PostMapping("/recompute")
    public Map<String, Object> recompute() {
        return analyticsService.recomputeAll();
    }
}

