package com.example.bigdata_news.service;


import com.example.bigdata_news.entity.News;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewsService {

    List<News> findAll();

    News findById(String uniquekey);

    News save(News news);

    void deleteById(String uniquekey);

    Page<News> findPage(int page, int size);
}