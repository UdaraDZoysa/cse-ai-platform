package com.harsha.investment_intelligence_service.application.api.dto.stock;

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
