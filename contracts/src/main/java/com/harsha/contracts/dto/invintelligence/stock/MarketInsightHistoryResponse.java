package com.harsha.contracts.dto.invintelligence.stock;

import java.time.Instant;

public record MarketInsightHistoryResponse(
        String id,
        String symbol,
        String companyName,
        String summary,
        String sentiment,
        double importanceScore,
        Instant generatedAt
) {
}
