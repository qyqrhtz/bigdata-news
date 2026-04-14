package com.example.bigdata_news.controller;

import com.example.bigdata_news.service.CozeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Coze 测试接口
 * 用于验证 Coze 工作流是否正常调用
 */
@RestController
@RequestMapping("/api/test/coze")
@CrossOrigin
public class CozeTestController {

    private final CozeService cozeService;

    // 构造器注入
    public CozeTestController(CozeService cozeService) {
        this.cozeService = cozeService;
    }

    /**
     * GET 测试接口（浏览器可直接访问）
     * 示例：
     * http://localhost:8081/api/test/coze/simple?question=北京天气怎么样
     */
    @GetMapping("/simple")
    public String simpleTest(@RequestParam String question) {

        // 模拟一段新闻上下文（RAG数据）
        String context = """
                北京今日天气晴朗，最高气温25摄氏度，最低气温15摄氏度。
                上海出现降雨天气，气温有所下降。
                广州天气炎热，市民注意防暑降温。
                """;

        return cozeService.callCoze(question, context);
    }

    /**
     * POST 测试接口（推荐，用于模拟真实RAG）
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> request) {

        String question = request.get("question");

        // 模拟RAG上下文
        String context = """
                【新闻1】北京今日天气晴朗，最高气温25℃。
                【新闻2】上海今日有中雨，气温下降。
                【新闻3】广州持续高温，达到33℃。
                """;

        String answer = cozeService.callCoze(question, context);

        Map<String, Object> result = new HashMap<>();
        result.put("question", question);
        result.put("answer", answer);
        result.put("context", context);

        return result;
    }
}