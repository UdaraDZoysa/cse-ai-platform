package com.harsha.contracts.events.analysis;

public record VolatilityFeatures(
        double standardDeviation,
        double variance,
        VolatilityRegime regime
) {
}
