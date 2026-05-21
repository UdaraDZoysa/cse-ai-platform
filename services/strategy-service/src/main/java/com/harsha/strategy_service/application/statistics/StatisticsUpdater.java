package com.harsha.strategy_service.application.statistics;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import org.springframework.stereotype.Component;

@Component
public class StatisticsUpdater {
    public void update(
            SymbolStatisticsState state,
            StockFeatureEvent event
    ) {
        long n = state.getSampleCount();

        double currentReturn = event.momentum().cumulativeReturn();

        double currentVolatility = event.volatility().standardDeviation();

        double currentMomentum = Math.abs(event.momentum().averageReturn());

        //RETURN
        double returnDelta = currentReturn - state.getMeanReturn();

        double newReturnMean = state.getMeanReturn() + (returnDelta / (n + 1));

        double newReturnVariance =
                ((n * state.getReturnStdDev() * state.getReturnStdDev()) +
                                (returnDelta * (currentReturn - newReturnMean))) / (n + 1);

        //VOLATILITY
       double volatilityDelta = currentVolatility - state.getMeanVolatility();

        double newVolatilityMean = state.getMeanVolatility() + (volatilityDelta / (n + 1));

        double newVolatilityVariance =
                ((n * state.getVolatilityStdDev() * state.getVolatilityStdDev()) +
                        (volatilityDelta * (currentVolatility - newVolatilityMean))) / (n + 1);

        //MOMENTUM
        double momentumDelta = currentMomentum - state.getMeanMomentumStrength();

        double newMomentumMean = state.getMeanMomentumStrength() + (momentumDelta / (n + 1));

        double newMomentumVariance =
                ((n * state.getMomentumStdDev() * state.getMomentumStdDev()) +
                        (momentumDelta * (currentMomentum - newMomentumMean))) / (n + 1);

        //SET VALUES
        state.setMeanReturn(newReturnMean);

        state.setReturnVariance(
                Math.max(newReturnVariance, 0)
        );

        state.setMeanVolatility(newVolatilityMean);

        state.setVolatilityVariance(
                Math.max(newVolatilityVariance, 0)
        );

        state.setMeanMomentumStrength(newMomentumMean);

        state.setMomentumVariance(
                Math.max(newMomentumVariance, 0)
        );

        state.incrementSampleCount();
    }
}
