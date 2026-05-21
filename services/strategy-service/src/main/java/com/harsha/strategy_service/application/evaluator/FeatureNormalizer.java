package com.harsha.strategy_service.application.evaluator;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.NormalizedFeatureSet;
import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import org.springframework.stereotype.Component;

@Component
public class FeatureNormalizer {
    public NormalizedFeatureSet normalize(
            StockFeatureEvent event,
            SymbolStatisticsState statistics
    ) {
        double returnZ = zScore(
                event.momentum().cumulativeReturn(),
                statistics.getMeanReturn(),
                statistics.getReturnStdDev()
        );

        double volatilityZ = zScore(
                event.volatility().standardDeviation(),
                statistics.getMeanVolatility(),
                statistics.getVolatilityStdDev()
        );

        double momentumZ = zScore(
                Math.abs(event.momentum().averageReturn()),
                statistics.getMeanMomentumStrength(),
                statistics.getMomentumStdDev()
        );

        return new NormalizedFeatureSet(
                returnZ,
                volatilityZ,
                momentumZ
        );
    }

    private double zScore(
            double value,
            double mean,
            double stdDev
    ) {
        if (Double.isNaN(stdDev) || stdDev == 0) {
            return 0;
        }

        return (value - mean) / stdDev;
    }
}
