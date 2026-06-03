package com.harsha.investment_intelligence_service.domain.model.reasoning;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.domain.model.summary.StrategySummary;
import com.harsha.investment_intelligence_service.domain.model.summary.TransitionSummary;

import java.util.List;

public record AIReasoningContext(
        String symbol,
        StockFeatureEvent latestFeature,
        StrategySummary strategySummary,
        TransitionSummary transitionSummary,
        List<MarketInsightGeneratedEvent> activeInsights
) {
}
