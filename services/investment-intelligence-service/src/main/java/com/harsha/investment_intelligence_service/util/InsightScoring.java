package com.harsha.investment_intelligence_service.util;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;

public class InsightScoring {
    private static final double
            IMPORTANCE_WEIGHT = 0.7;

    private static final double
            CONFIDENCE_WEIGHT = 0.3;

    private InsightScoring() {
    }

    public static double score(
            MarketInsightGeneratedEvent insight
    ) {
        return insight.importanceScore() * IMPORTANCE_WEIGHT
                + insight.confidenceScore() * CONFIDENCE_WEIGHT;
    }
}
