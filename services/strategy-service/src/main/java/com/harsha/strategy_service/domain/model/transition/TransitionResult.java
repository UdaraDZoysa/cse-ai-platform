package com.harsha.strategy_service.domain.model.transition;

import com.harsha.contracts.events.strategy.TransitionReason;

import java.util.Set;

public record TransitionResult(
        Set<TransitionReason> reasons
) {
    public boolean detected() {
        return !reasons.isEmpty();
    }
    public static TransitionResult none() {
        return new TransitionResult(Set.of());
    }
}
