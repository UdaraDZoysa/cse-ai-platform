package com.harsha.contracts.events.analysis;

public record TrendFeatures(
        double upwardRatio,
        double downwardRatio,
        double persistence,
        TrendDirection direction
) {
}
