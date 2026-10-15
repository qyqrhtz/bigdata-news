package com.example.bigdata_news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Application health and dependency configuration summary")
public record HealthResponse(
        @Schema(description = "Application status", example = "UP")
        String status,

        @Schema(description = "Application name", example = "bigdata-news")
        String service,

        @Schema(description = "Current server timestamp")
        Instant timestamp,

        @Schema(description = "Dependency configuration summary")
        Map<String, String> dependencies
) {
}
