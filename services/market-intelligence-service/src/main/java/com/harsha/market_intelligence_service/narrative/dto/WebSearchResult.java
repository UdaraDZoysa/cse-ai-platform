package com.harsha.market_intelligence_service.narrative.dto;

public record WebSearchResult(
        String title,
        String content,
        String sourceUrl,
        String publishedDate
) {
}
