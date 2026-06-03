package com.harsha.investment_intelligence_service.domain.model.summary;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;

public record TransitionSummary(
        int bullishTransitions,
        int bearishTransitions,
        int reversals,
        OpportunityTransitionEvent latestTransition
) {
}
