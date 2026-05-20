package com.harsha.strategy_service.application.evaluator;

import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.SignalDirection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SignalFusionEngine {
    public double calculateConfidence(
            List<DetectorSignal> signals
    ) {
        double bullish = 0;

        double bearish = 0;

        for (DetectorSignal signal : signals) {
            if (!signal.valid()) {
                continue;
            }

            double weightedStrength =
                    signal.strength() * signal.reliability();

            if (signal.direction() == SignalDirection.BULLISH) {
                bullish += weightedStrength;
            }

            if (signal.direction() == SignalDirection.BEARISH) {
                bearish += weightedStrength;
            }
        }
        return Math.max(
                bullish,
                bearish
        );
    }

    public SignalDirection determineDirection(
            List<DetectorSignal> signals
    ) {
        double bullish = 0;

        double bearish = 0;

        for (DetectorSignal signal : signals) {
            if (!signal.valid()) {
                continue;
            }

            double weightedStrength =
                    signal.strength() * signal.reliability();

            if (signal.direction() == SignalDirection.BULLISH) {
                bullish += weightedStrength;
            }

            if (signal.direction() == SignalDirection.BEARISH) {
                bearish += weightedStrength;
            }
        }

        if (bullish > bearish) {
            return SignalDirection.BULLISH;
        }

        if (bearish > bullish) {
            return SignalDirection.BEARISH;
        }

        return SignalDirection.NEUTRAL;
    }
}
