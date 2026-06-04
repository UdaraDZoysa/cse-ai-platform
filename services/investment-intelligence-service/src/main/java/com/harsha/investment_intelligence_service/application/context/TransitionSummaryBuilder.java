package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.events.strategy.SignalDirection;
import com.harsha.contracts.events.strategy.TransitionReason;
import com.harsha.investment_intelligence_service.domain.model.summary.TransitionSummary;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class TransitionSummaryBuilder {
    public TransitionSummary build(
            Deque<OpportunityTransitionEvent> history
    ) {
        if (history.isEmpty()) {
            return null;
        }

        int bullishTransitions = 0;
        int bearishTransitions = 0;

        int confidenceIncreases = 0;
        int confidenceDecreases = 0;

        int reversals = 0;

        SignalDirection previousDirection = null;

        for (OpportunityTransitionEvent event : history) {
            if (event.currentDirection() == SignalDirection.BULLISH) {
                bullishTransitions++;
            }

            if (event.currentDirection() == SignalDirection.BEARISH) {
                bearishTransitions++;
            }

            if (event.reasons().contains(TransitionReason.CONFIDENCE_INCREASE)) {
                confidenceIncreases++;
            }

            if (event.reasons().contains(TransitionReason.CONFIDENCE_DECREASE)) {
                confidenceDecreases++;
            }

            if (previousDirection != null
                    && previousDirection != event.currentDirection()
            ) {
                reversals++;
            }

            previousDirection = event.currentDirection();
        }

        return new TransitionSummary(
                history.size(),
                bullishTransitions,
                bearishTransitions,
                reversals,
                confidenceIncreases,
                confidenceDecreases,
                history.getLast()
        );
    }
}
