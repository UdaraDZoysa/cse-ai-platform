package com.harsha.strategy_service.application.statistics;

import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import org.springframework.stereotype.Component;

@Component
public class StatisticalReadinessEvaluator {
    //Minimum sample size for statistical warm
    private static final long MINIMUM_SAMPLE_SIZE = 50;

    public boolean ready(
            SymbolStatisticsState state
    ) {

        if (state == null) {
            return false;
        }

        return state.getSampleCount()
                >=
                MINIMUM_SAMPLE_SIZE;
    }
}
