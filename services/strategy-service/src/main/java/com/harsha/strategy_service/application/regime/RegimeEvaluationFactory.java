package com.harsha.strategy_service.application.regime;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.strategy_service.domain.model.RegimeEvaluation;
import com.harsha.strategy_service.domain.model.RegimeState;
import org.springframework.stereotype.Component;

@Component
public class RegimeEvaluationFactory {
    public RegimeEvaluation evaluate(
            RegimeState regimeState
    ) {
        MarketRegime regime = regimeState.regime();

        switch (regime) {
            case TRENDING -> {
                return new RegimeEvaluation(
                        regimeState,
                        1.3,
                        1.1,
                        0.9,
                        0.7
                );
            }

            case BREAKOUT_EXPANSION -> {
                return new RegimeEvaluation(
                        regimeState,
                        1.0,
                        1.3,
                        1.5,
                        0.8
                );
            }

            case VOLATILE -> {
                return new RegimeEvaluation(
                        regimeState,
                        0.8,
                        0.9,
                        1.1,
                        1.3
                );
            }

            case PANIC -> {
                return new RegimeEvaluation(
                        regimeState,
                        0.5,
                        0.6,
                        0.7,
                        1.5
                );
            }

            default -> {
                return new RegimeEvaluation(
                        regimeState,
                        0.7,
                        0.7,
                        0.6,
                        1.0
                );
            }
        }
    }
}
