package com.harsha.strategy_service.application.regime;

import com.harsha.strategy_service.domain.model.MarketRegime;
import com.harsha.strategy_service.domain.model.NormalizedFeatureSet;
import com.harsha.strategy_service.domain.model.RegimeState;
import org.springframework.stereotype.Component;

@Component
public class MarketRegimeDetector {
    public RegimeState detect(
            NormalizedFeatureSet features
    ) {
        if (features.volatilityZScore() > 3) {
            return new RegimeState(
                    MarketRegime.PANIC,
                    0.9
            );
        }

        if (features.momentumZScore() > 2 &&
                features.returnZScore() > 2) {
            return new RegimeState(
                    MarketRegime.BREAKOUT_EXPANSION,
                    0.8
            );
        }

        if (features.volatilityZScore() > 1.5) {
            return new RegimeState(
                    MarketRegime.VOLATILE,
                    0.7
            );
        }

        if (Math.abs(features.returnZScore()) > 1) {
            return new RegimeState(
                    MarketRegime.TRENDING,
                    0.7
            );
        }

        return new RegimeState(
                MarketRegime.SIDEWAYS,
                0.6
        );
    }
}
