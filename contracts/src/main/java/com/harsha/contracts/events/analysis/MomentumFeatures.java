package com.harsha.contracts.events.analysis;

public record MomentumFeatures(
        double cumulativeReturn,
        double averageReturn,
        double returnStdDev,
        double acceleration,
        double positiveMoveRatio,
        double negativeMoveRatio,
        double momentumPersistence,
        double largestPositiveReturn,
        double largestNegativeReturn,
        double efficiencyRatio
) {
    public static MomentumFeatures empty() {
        return new MomentumFeatures(
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN
        );
    }
}
