package com.harsha.contracts.events.analysis;

public record VolatilityFeatures(
        Double standardDeviation,
        Double variance,
        VolatilityRegime regime
) {
}
