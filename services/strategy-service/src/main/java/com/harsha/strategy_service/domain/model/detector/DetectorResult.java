package com.harsha.strategy_service.domain.model.detector;

import com.harsha.contracts.events.strategy.SignalDirection;

public record DetectorResult(
        String detectorType,
        double strength,
        SignalDirection direction
) {
}
