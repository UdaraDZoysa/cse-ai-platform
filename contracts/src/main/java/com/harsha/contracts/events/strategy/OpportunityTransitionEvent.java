package com.harsha.contracts.events.strategy;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

import java.util.Set;

public record OpportunityTransitionEvent(
        String symbol,
        long occurredAt,

        OpportunityStatus previousStatus,
        OpportunityStatus currentStatus,

        double previousConfidence,
        double currentConfidence,

        SignalDirection previousDirection,
        SignalDirection currentDirection,

        MarketRegime previousRegime,
        MarketRegime currentRegime,

        Set<TransitionReason> reasons
) implements DomainEvent {
    @Override
    public EventType eventType() {
        return EventType.OPPORTUNITY_TRANSITION_EVENT;
    }
}
