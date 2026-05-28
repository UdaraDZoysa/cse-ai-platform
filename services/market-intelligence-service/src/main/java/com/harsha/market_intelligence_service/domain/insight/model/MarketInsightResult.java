package com.harsha.market_intelligence_service.domain.insight.model;

public record MarketInsightResult(
        String summary,
        String reasoning,
        NarrativeSentiment sentiment,
        double importanceScore,
        double persistenceScore,
        double confidenceScore
) {
}
