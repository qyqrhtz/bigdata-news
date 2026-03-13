package com.example.bigdata_news.repository;

import com.example.bigdata_news.entity.AnalyticsResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsResultRepository extends JpaRepository<AnalyticsResult, String> {
}

