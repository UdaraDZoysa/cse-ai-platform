package com.harsha.strategy_service.application.transition;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.strategy_service.domain.model.OpportunitySnapshot;
import com.harsha.contracts.events.strategy.TransitionReason;
import com.harsha.strategy_service.domain.model.transition.TransitionResult;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class OpportunityTransitionEvaluator {
    private static final double CONFIDENCE_JUMP_THRESHOLD = 20;

    public TransitionResult evaluate(
            OpportunitySnapshot previous,
            OpportunitySnapshot current
    ) {
        if (previous == null) {
            return TransitionResult.none();
        }

        Set<TransitionReason> reasons =
                EnumSet.noneOf(
                        TransitionReason.class
                );

        //STATUS TRANSITION
        if (previous.status() != current.status()) {
            reasons.add(
                    TransitionReason.STATUS_CHANGED
            );
        }

        //DIRECTION TRANSITION
        if (previous.direction() != current.direction()) {
            reasons.add(
                    TransitionReason.DIRECTION_CHANGED
            );
        }

        //CONFIDENCE TRANSITION
        double delta = current.confidence() - previous.confidence();

        if (delta >= CONFIDENCE_JUMP_THRESHOLD) {
            reasons.add(TransitionReason.CONFIDENCE_INCREASE);
        }

        if (delta <= -CONFIDENCE_JUMP_THRESHOLD) {
            reasons.add(TransitionReason.CONFIDENCE_DECREASE);
        }

        //REGIME TRANSITION
        if (importantRegimeChange(previous.marketRegime(), current.marketRegime())) {
            reasons.add(
                    TransitionReason.REGIME_CHANGED
            );
        }
        return reasons.isEmpty()
                ? TransitionResult.none()
                : new TransitionResult(reasons);
    }

    private boolean importantRegimeChange(
            MarketRegime previous,
            MarketRegime current
    ) {
        if (previous == null || current == null) {
            return false;
        }

        return previous != current;
    }
}
