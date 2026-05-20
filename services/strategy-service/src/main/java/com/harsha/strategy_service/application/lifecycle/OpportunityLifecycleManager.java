package com.harsha.strategy_service.application.lifecycle;

import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.domain.model.OpportunityStatus;
import org.springframework.stereotype.Component;

@Component
public class OpportunityLifecycleManager {
    public void evaluate(
            OpportunityState state
    ) {
        double confidence = state.getConfidence();

        if (confidence >= 70) {
            state.setStatus(
                    OpportunityStatus.STRENGTHENED
            );
            return;
        }

        if (confidence >= 35) {
            state.setStatus(
                    OpportunityStatus.OPENED
            );
            return;
        }

        if (confidence >= 10) {
            state.setStatus(
                    OpportunityStatus.WEAKENED
            );
            return;
        }

        state.setStatus(
                OpportunityStatus.INVALIDATED
        );
    }
}
