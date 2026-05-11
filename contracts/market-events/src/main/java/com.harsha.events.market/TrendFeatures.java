package com.harsha.events.market;

public record TrendFeatures(
        double upwardRatio,
        double downwardRatio,
        double persistence,
        String direction
) {
}
