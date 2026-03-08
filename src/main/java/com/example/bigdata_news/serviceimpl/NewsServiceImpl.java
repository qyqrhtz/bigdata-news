package com.example.bigdata_news.serviceimpl;



import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.repository.NewsRepository;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    @Override
    public News findById(String uniquekey) {
        return newsRepository.findById(uniquekey).orElse(null);
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteById(String uniquekey) {
        newsRepository.deleteById(uniquekey);
    }
    @Override
    public Page<News> findPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return newsRepository.findAll(pageable);
    }
}
