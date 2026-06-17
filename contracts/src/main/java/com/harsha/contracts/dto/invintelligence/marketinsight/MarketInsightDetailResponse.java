package com.harsha.contracts.dto.invintelligence.marketinsight;

import java.time.Instant;

public record MarketInsightDetailResponse(
        String id,
        String symbol,
        String companyName,
        String summary,
        String reasoning,
        String sentiment,
        double importanceScore,
        double confidenceScore,
        double persistenceScore,
        String generatedBy,
        Instant occurredAt
) {
}
