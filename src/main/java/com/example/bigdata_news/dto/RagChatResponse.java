package com.example.bigdata_news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "RAG chat answer with retrieved source news")
public record RagChatResponse(
        @Schema(description = "Original user question")
        String question,
        @Schema(description = "Generated answer")
        String answer,
        @Schema(description = "Retrieved news used as answer sources")
        List<RagSearchResult> sources
) {
}
