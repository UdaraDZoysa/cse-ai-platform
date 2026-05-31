package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.SignalDirection;

public record DetectorSignal(
        DetectorType source,
        double strength,
        double reliability,
        SignalDirection direction,
        boolean valid
) {
    public static DetectorSignal invalid(
            DetectorType source
    ) {
        return new DetectorSignal(
                source,
                Double.NaN,
                Double.NaN,
                SignalDirection.NEUTRAL,
                false
        );
    }
}
