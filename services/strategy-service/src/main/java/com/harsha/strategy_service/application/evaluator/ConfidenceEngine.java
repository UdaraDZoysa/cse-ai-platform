package com.harsha.strategy_service.application.evaluator;

import com.harsha.strategy_service.domain.model.OpportunityState;
import org.springframework.stereotype.Component;

@Component
public class ConfidenceEngine {
    private static final double DECA_FACTOR = 0.97;
    public void update(
            OpportunityState state,
            double incomingConfidence
    ) {

        double current = state.getConfidence();

        current *= DECA_FACTOR;

        current += incomingConfidence;

        current = Math.min(current, 1.0);

        if (current < 0) {
            current = 0;
        }

        state.setConfidence(current);

        state.incrementPersistence();

        state.updateTimestamp();
    }
}
