package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.SignalDirection;

public record DetectorResult(
        String detectorType,
        double strength,
        SignalDirection direction
) {
}
