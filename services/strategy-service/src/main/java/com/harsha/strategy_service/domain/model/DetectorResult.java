package com.harsha.strategy_service.domain.model;

public record DetectorResult(
        String detectorType,
        double strength,
        SignalDirection direction
) {
}
