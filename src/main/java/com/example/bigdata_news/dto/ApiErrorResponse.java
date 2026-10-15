package com.example.bigdata_news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Unified API error response")
public record ApiErrorResponse(
        @Schema(description = "Error timestamp")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP status reason", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable error detail", example = "question: question must not be blank")
        String message,
        @Schema(description = "Request path", example = "/api/news/rag/chat")
        String path
) {
}
