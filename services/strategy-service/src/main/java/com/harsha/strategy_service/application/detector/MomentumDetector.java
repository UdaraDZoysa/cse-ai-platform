package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.DetectorType;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.springframework.stereotype.Component;

@Component
public class MomentumDetector implements Detector {

    @Override
    public DetectorSignal detect(
            StockFeatureEvent event
    ) {
        var momentum = event.momentum();

        if (Double.isNaN(momentum.averageReturn())) {
            return DetectorSignal.invalid(
                    DetectorType.MOMENTUM
            );
        }

        double directionalStrength =
                momentum.cumulativeReturn() * momentum.efficiencyRatio() * momentum.momentumPersistence();

        double accelerationBoost = Math.max(
                0,
                momentum.acceleration()
        );

        double strength = directionalStrength + accelerationBoost;

        double reliability = Math.min(
                1.0,
                momentum.efficiencyRatio() + momentum.momentumPersistence()
        );

        SignalDirection direction =
                strength >= 0 ? SignalDirection.BULLISH : SignalDirection.BEARISH;

        return new DetectorSignal(
                DetectorType.MOMENTUM,
                Math.abs(strength),
                reliability,
                direction,
                true
        );
    }
}
