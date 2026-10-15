package com.example.bigdata_news.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CozeService {

    private static final Logger log = LoggerFactory.getLogger(CozeService.class);

    private static final String URL = "https://api.coze.cn/v1/workflow/run";

    @Value("${coze.token}")
    private String token;

    @Value("${coze.workflow-id}")
    private String workflowId;

    private final RestTemplate restTemplate;

    public CozeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @SuppressWarnings("unchecked")
    public String callCoze(String question, String context) {
        if (token == null || token.isBlank() || workflowId == null || workflowId.isBlank()) {
            log.warn("Coze token or workflow id is not configured; skip remote workflow call");
            return "Coze 服务未配置，请设置 COZE_TOKEN 和 COZE_WORKFLOW_ID 后重试。";
        }

        // 1. 构造 Prompt（RAG 核心）
        String prompt = String.format("""
                你是一个新闻分析助手，请基于提供的新闻内容回答问题。
                如果无法从内容中找到答案，请说“根据现有新闻无法确定”。

                【新闻内容】
                %s

                【问题】
                %s
                """, context, question);

        // 2. 构造请求参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("question", question);
        parameters.put("context", context);
        parameters.put("prompt", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("workflow_id", workflowId);
        body.put("parameters", parameters);  // ✅ 必须有 parameters

        // 3. 构造 Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // 4. 调用 Coze API
            ResponseEntity<Map> response = restTemplate.exchange(URL, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                log.error("Coze返回为空");
                return "Coze返回为空";
            }

            log.info("Coze原始返回: {}", responseBody);

            Object dataObj = responseBody.get("data");

            if (dataObj == null) {
                log.error("Coze返回data为空");
                return "Coze返回data为空";
            }

            // 5. 解析 data
            if (dataObj instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) dataObj;
                if (data.containsKey("output")) {
                    return data.get("output").toString();
                } else if (data.containsKey("outputs")) {
                    return data.get("outputs").toString();
                } else {
                    log.error("未知返回结构: {}", data);
                    return "未知返回结构：" + data;
                }
            } else if (dataObj instanceof List) {
                List<?> dataList = (List<?>) dataObj;
                return dataList.toString();
            } else {
                // 其他类型，直接转字符串
                return dataObj.toString();
            }

        } catch (Exception e) {
            log.error("调用Coze失败", e);
            return "调用Coze失败：" + e.getMessage();
        }
    }
}
