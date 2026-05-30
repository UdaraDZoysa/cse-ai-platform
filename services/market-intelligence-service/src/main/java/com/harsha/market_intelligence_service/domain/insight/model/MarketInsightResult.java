package com.harsha.market_intelligence_service.domain.insight.model;

import com.harsha.contracts.events.market_intelligence.NarrativeSentiment;

public record MarketInsightResult(
        String summary,
        String reasoning,
        NarrativeSentiment sentiment,
        double importanceScore,
        double persistenceScore,
        double confidenceScore
) {
}
