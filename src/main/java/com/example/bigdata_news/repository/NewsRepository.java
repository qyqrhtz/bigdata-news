package com.example.bigdata_news.repository;


import com.example.bigdata_news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, String> {
    // 这里可以加自定义查询方法，例如按 category 查新闻
}