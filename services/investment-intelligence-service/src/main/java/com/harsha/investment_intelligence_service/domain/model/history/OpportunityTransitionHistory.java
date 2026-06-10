package com.harsha.investment_intelligence_service.domain.model.history;

import com.harsha.contracts.events.strategy.OpportunityStatus;

import java.time.Instant;

public record OpportunityTransitionHistory(
        String symbol,
        OpportunityStatus fromStatus,
        OpportunityStatus toStatus,
        String reason,
        Instant occurredAt
) {
}
