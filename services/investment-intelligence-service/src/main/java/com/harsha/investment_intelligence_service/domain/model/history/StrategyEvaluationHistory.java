package com.harsha.investment_intelligence_service.domain.model.history;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;

import java.time.Instant;

public record StrategyEvaluationHistory(
        String symbol,
        double confidence,
        OpportunityStatus status,
        SignalDirection direction,
        MarketRegime marketRegime,
        int persistenceCount,
        Instant occurredAt
) {
}
