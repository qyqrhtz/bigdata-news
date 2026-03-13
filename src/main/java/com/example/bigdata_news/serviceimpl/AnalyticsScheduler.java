package com.example.bigdata_news.serviceimpl;

import com.example.bigdata_news.service.AnalyticsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsScheduler {

    private final AnalyticsService analyticsService;

    public AnalyticsScheduler(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Daily at 02:10
    @Scheduled(cron = "${analytics.schedule.cron:0 10 2 * * *}")
    public void recomputeNightly() {
        analyticsService.recomputeAll();
    }
}

