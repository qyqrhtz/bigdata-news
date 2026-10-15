package com.example.bigdata_news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "RAG chat request")
public record RagChatRequest(
        @Schema(description = "User question to answer with retrieved news context", example = "What are the main climate news topics?")
        @NotBlank(message = "question must not be blank")
        String question
) {
}
