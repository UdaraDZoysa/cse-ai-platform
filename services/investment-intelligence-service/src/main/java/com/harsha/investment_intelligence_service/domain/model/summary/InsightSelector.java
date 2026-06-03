package com.harsha.investment_intelligence_service.domain.model.summary;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.util.InsightScoring;

import java.util.Comparator;
import java.util.List;

public final class InsightSelector {
    private InsightSelector() {
    }

    public static List<MarketInsightGeneratedEvent> topInsights(
            List<MarketInsightGeneratedEvent> insights,
            int limit
    ) {
        return insights.stream()
                .sorted(Comparator
                        .comparingDouble(
                                InsightScoring::score
                        )
                        .reversed()
                )
                .limit(limit)
                .toList();
    }
}
