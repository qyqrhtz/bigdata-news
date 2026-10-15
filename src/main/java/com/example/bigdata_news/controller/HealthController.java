package com.example.bigdata_news.controller;

import com.example.bigdata_news.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Application liveness and dependency configuration")
public class HealthController {

    private final String ragServiceUrl;
    private final String cozeWorkflowId;

    public HealthController(@Value("${rag.service-url}") String ragServiceUrl,
                            @Value("${coze.workflow-id:}") String cozeWorkflowId) {
        this.ragServiceUrl = ragServiceUrl;
        this.cozeWorkflowId = cozeWorkflowId;
    }

    @Operation(summary = "Application health", description = "Returns liveness status and non-sensitive dependency configuration.")
    @GetMapping
    public HealthResponse health() {
        Map<String, String> dependencies = new LinkedHashMap<>();
        dependencies.put("ragServiceUrl", ragServiceUrl);
        dependencies.put("cozeWorkflowConfigured", String.valueOf(cozeWorkflowId != null && !cozeWorkflowId.isBlank()));

        return new HealthResponse("UP", "bigdata-news", Instant.now(), dependencies);
    }
}
