package com.harsha.contracts.events.market;

public record TrendFeatures(
        double upwardRatio,
        double downwardRatio,
        double persistence,
        String direction
) {
}
