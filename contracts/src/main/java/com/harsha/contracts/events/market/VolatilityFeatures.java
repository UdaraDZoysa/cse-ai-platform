package com.harsha.contracts.events.market;

public record VolatilityFeatures(
        double standardDeviation,
        double variance,
        String regime
) {
}
