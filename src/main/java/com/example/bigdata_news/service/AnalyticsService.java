package com.example.bigdata_news.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getTopics();

    Map<String, Object> getAuthorAnomalies();

    Map<String, Object> getDuplicates();

    Map<String, Object> getBursts();

    Map<String, Object> recomputeAll();
}

