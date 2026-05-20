package com.harsha.strategy_service.application.evaluator;

import com.harsha.strategy_service.domain.model.OpportunityState;
import org.springframework.stereotype.Component;

@Component
public class ConfidenceEngine {
    public void update(
            OpportunityState state,
            double incomingConfidence
    ) {

        double current = state.getConfidence();

        current *= 0.97;

        current += incomingConfidence;

        current = Math.min(current, 100);

        if (current < 0) {
            current = 0;
        }

        state.setConfidence(current);

        state.incrementPersistence();

        state.updateTimestamp();
    }
}
