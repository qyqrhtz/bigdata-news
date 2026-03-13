package com.example.bigdata_news.controller;

import com.example.bigdata_news.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/topics")
    public Map<String, Object> topics() {
        return analyticsService.getTopics();
    }

    @GetMapping("/authors/anomaly")
    public Map<String, Object> authorAnomaly() {
        return analyticsService.getAuthorAnomalies();
    }

    @GetMapping("/duplicates")
    public Map<String, Object> duplicates() {
        return analyticsService.getDuplicates();
    }

    @GetMapping("/bursts")
    public Map<String, Object> bursts() {
        return analyticsService.getBursts();
    }

    @PostMapping("/recompute")
    public Map<String, Object> recompute() {
        return analyticsService.recomputeAll();
    }
}

