package com.harsha.events.market;

public record VolatilityFeatures(
        double standardDeviation,
        double variance,
        String regime
) {
}