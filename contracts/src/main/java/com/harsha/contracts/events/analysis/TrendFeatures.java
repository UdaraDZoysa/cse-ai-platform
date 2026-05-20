package com.harsha.contracts.events.analysis;

public record TrendFeatures(
        Double upwardRatio,
        Double downwardRatio,
        Double persistence,
        TrendDirection direction
) {
}
