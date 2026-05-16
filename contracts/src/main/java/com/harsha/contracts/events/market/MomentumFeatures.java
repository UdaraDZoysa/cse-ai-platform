package com.harsha.contracts.events.market;

public record MomentumFeatures(
        double cumulativeReturn,
        double averageReturn,
        double returnStdDev,
        double averageDelta,
        double acceleration,
        double positiveMoveRatio,
        double negativeMoveRatio,
        double momentumPersistence,
        double largestUpMove,
        double largestDownMove,
        double efficiencyRatio
) {
    public static MomentumFeatures empty() {
        return new MomentumFeatures(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
    }
}
