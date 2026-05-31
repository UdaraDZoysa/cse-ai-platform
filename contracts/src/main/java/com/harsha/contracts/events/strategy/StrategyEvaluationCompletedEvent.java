package com.harsha.contracts.events.strategy;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

public record StrategyEvaluationCompletedEvent(
        String symbol,
        long occurredAt,
        MarketRegime marketRegime,
        double regimeConfidence,
        double confidence,
        SignalDirection direction,
        OpportunityStatus status,
        int persistence,
        boolean statisticalReady,
        int sampleCount
) implements DomainEvent {
    @Override
    public EventType eventType() {
        return EventType.STRATEGY_EVALUATION_COMPLETED_EVENT;
    }
}
