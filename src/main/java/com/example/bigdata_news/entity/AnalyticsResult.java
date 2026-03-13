package com.example.bigdata_news.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_result")
public class AnalyticsResult {

    @Id
    @Column(length = 64)
    private String resultKey;

    private LocalDateTime updatedAt;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String json;

    public AnalyticsResult() {
    }

    public AnalyticsResult(String resultKey, LocalDateTime updatedAt, String json) {
        this.resultKey = resultKey;
        this.updatedAt = updatedAt;
        this.json = json;
    }

    public String getResultKey() {
        return resultKey;
    }

    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}

