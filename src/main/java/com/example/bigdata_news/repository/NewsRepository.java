package com.example.bigdata_news.repository;

import com.example.bigdata_news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface NewsRepository extends JpaRepository<News, String>, JpaSpecificationExecutor<News> {

    interface NewsSlice {
        String getTitle();
        LocalDateTime getDate();
        String getCategory();
        String getAuthorName();
        String getUrl();
    }

    @Query("select n.title as title, n.date as date, n.category as category, n.authorName as authorName, n.url as url from News n")
    List<NewsSlice> findAllSlices();

    // 1. 各分类平均标题长度
    @Query(value = "SELECT category as name, AVG(CHAR_LENGTH(title)) as value FROM news GROUP BY category ORDER BY value DESC", nativeQuery = true)
    List<Map<String, Object>> findAvgHeadlineLengthByCategory();

    // 2. 跨领域作者分析 (核心：将列名改为 author_name)
    @Query(value = "SELECT author_name as name, COUNT(DISTINCT category) as value FROM news WHERE author_name IS NOT NULL AND author_name != '' GROUP BY author_name HAVING value > 1 ORDER BY value DESC LIMIT 15", nativeQuery = true)
    List<Map<String, Object>> findCrossDomainAuthors();

    // 3. 信息密度（当前数据表无 short_description，使用 title 长度近似）
    @Query(value = "SELECT CASE WHEN CHAR_LENGTH(title) < 50 THEN '短' WHEN CHAR_LENGTH(title) BETWEEN 50 AND 150 THEN '中' ELSE '长' END as name, COUNT(*) as value FROM news GROUP BY name", nativeQuery = true)
    List<Map<String, Object>> countByDescriptionDensity();

    // 4. 年度发稿分布
    @Query(value = "SELECT YEAR(date) as name, COUNT(*) as value FROM news WHERE date IS NOT NULL GROUP BY YEAR(date) ORDER BY name ASC", nativeQuery = true)
    List<Map<String, Object>> countByYear();

    // 5. 分类占比
    @Query(value = "SELECT category as name, COUNT(*) as value FROM news GROUP BY category", nativeQuery = true)
    List<Map<String, Object>> countByCategory();

    // 6. 核心作者发稿量 (核心：将列名改为 author_name)
    @Query(value = "SELECT author_name as name, COUNT(*) as value FROM news WHERE author_name IS NOT NULL AND author_name != '' GROUP BY author_name ORDER BY value DESC LIMIT 10", nativeQuery = true)
    List<Map<String, Object>> findTopAuthors();
}