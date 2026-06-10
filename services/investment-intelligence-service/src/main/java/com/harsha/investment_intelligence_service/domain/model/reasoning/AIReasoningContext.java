package com.harsha.investment_intelligence_service.domain.model.reasoning;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.domain.model.summary.StrategySummary;
import com.harsha.investment_intelligence_service.domain.model.summary.TransitionSummary;

import java.util.List;

public record AIReasoningContext(
        String symbol,
        MarketSnapshotEvent marketSnapshot,
        StrategySummary strategySummary,
        TransitionSummary transitionSummary,
        List<MarketInsightGeneratedEvent> activeInsights,
        List<StrategyEvaluationCompletedEvent> recentEvaluations,
        List<OpportunityTransitionEvent> recentTransitions
) {
}
