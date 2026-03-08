package com.example.bigdata_news.controller;

import com.example.bigdata_news.entity.News;
import com.example.bigdata_news.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<News> getAllNews() {
        return newsService.findAll();
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable String id) {
        return newsService.findById(id);
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        return newsService.save(news);
    }

    @GetMapping("/page")
    public Page<News> getNewsPage(
            @RequestParam int page,
            @RequestParam int size) {

        return newsService.findPage(page, size);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteById(id);
    }
}