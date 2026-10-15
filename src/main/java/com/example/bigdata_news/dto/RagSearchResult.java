package com.example.bigdata_news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Semantic retrieval result returned by the FAISS RAG service")
public record RagSearchResult(
        @Schema(description = "Ranking position in the current retrieval result", example = "1")
        Integer rank,
        @Schema(description = "News primary key", example = "b6e9d0f5c7")
        String uniquekey,
        @Schema(description = "Matched news title", example = "Climate policy shifts in global summit")
        String title,
        @Schema(description = "Original news URL", example = "https://example.com/news/1")
        String url,
        @Schema(description = "FAISS L2 distance. Lower is more similar.", example = "0.42")
        Double score
) {
}
