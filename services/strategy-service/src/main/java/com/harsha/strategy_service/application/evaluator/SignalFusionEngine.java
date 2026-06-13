package com.harsha.strategy_service.application.evaluator;

import com.harsha.strategy_service.domain.model.detector.DetectorSignal;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SignalFusionEngine {
    private static final Logger log = LoggerFactory.getLogger(SignalFusionEngine.class);
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

            log.info(
                    "$$$$$$$$$$$$$$$$$$$$$$$$$source={} direction={} strength={} reliability={} weighted={}",
                    signal.source(),
                    signal.direction(),
                    signal.strength(),
                    signal.reliability(),
                    weightedStrength
            );

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
