package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.analysis.TrendDirection;
import com.harsha.contracts.events.analysis.VolatilityRegime;
import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.DetectorType;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.springframework.stereotype.Component;

@Component
public class BreakoutDetector implements Detector {
    @Override
    public DetectorSignal detect(
            StockFeatureEvent event
    ) {
        var momentum = event.momentum();

        var trend = event.trend();

        var movingAverage = event.movingAverage();

        var volatility = event.volatility();

        if ( Double.isNaN(momentum.cumulativeReturn()) ||
                Double.isNaN(movingAverage.ema5Tick()) ||
                Double.isNaN(movingAverage.ema20Tick())
        ) {
            return DetectorSignal.invalid(
                    DetectorType.BREAKOUT
            );
        }

        double emaSpread =
                ( movingAverage.ema5Tick() - movingAverage.ema20Tick()) / movingAverage.ema20Tick();

        boolean bullishStructure = emaSpread > 0;

        boolean bearishStructure = emaSpread < 0;

        double momentumStrength =
                Math.abs(momentum.cumulativeReturn()) *
                momentum.efficiencyRatio() *
                momentum.momentumPersistence();

        double persistenceBoost = trend.persistence();

        double breakoutStrength =
                momentumStrength + persistenceBoost + Math.abs(emaSpread);

        double reliability = 0.5 + (momentum.efficiencyRatio() * 0.5);

        VolatilityRegime regime = volatility.regime();

        /*
         breakout prefers expanding volatility,
         but extreme volatility reduces reliability
        */

        if (regime == VolatilityRegime.HIGH) {
            breakoutStrength *= 1.15;
        }

        if (regime == VolatilityRegime.EXTREME) {
            reliability *= 0.7;
        }

        /*
         Bullish breakout
        */

        if (bullishStructure &&
                trend.direction() == TrendDirection.BULLISH &&
                    momentum.cumulativeReturn() > 0
        ) {
            return new DetectorSignal(
                    DetectorType.BREAKOUT,
                    breakoutStrength,
                    reliability,
                    SignalDirection.BULLISH,
                    true
            );
        }

        /*
         Bearish breakout
        */

        if (bearishStructure &&
                trend.direction() == TrendDirection.BEARISH &&
                    momentum.cumulativeReturn() < 0
        ) {
            return new DetectorSignal(
                    DetectorType.BREAKOUT,
                    breakoutStrength,
                    reliability,
                    SignalDirection.BEARISH,
                    true
            );
        }

        return new DetectorSignal(
                DetectorType.BREAKOUT,
                0,
                0.4,
                SignalDirection.NEUTRAL,
                true
        );
    }
}
