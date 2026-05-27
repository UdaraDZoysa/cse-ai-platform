package com.harsha.market_intelligence_service.narrative.dto;

import java.util.List;

public record NarrativeExtractionResult(
        String symbol,
        String company,
        String summary,
        List<WebSearchResult> sources
) {
}
