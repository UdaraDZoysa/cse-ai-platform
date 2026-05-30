package com.harsha.contracts.events.market_intelligence;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

public record MarketInsightGeneratedEvent(
        String symbol,
        long occurredAt,
        String company,
        String summary,
        String reasoning,
        NarrativeSentiment sentiment,
        double importanceScore,
        double confidenceScore,
        long expiresAt,
        String generatedBy

) implements DomainEvent {

    @Override
    public EventType eventType() {
        return EventType.MARKET_INSIGHT_GENERATED_EVENT;
    }
}
