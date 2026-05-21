package com.harsha.contracts.events.analysis;

public record MomentumFeatures(
        Double cumulativeReturn,
        Double averageReturn,
        Double returnStdDev,
        Double acceleration,
        Double positiveMoveRatio,
        Double negativeMoveRatio,
        Double momentumPersistence,
        Double largestPositiveReturn,
        Double largestNegativeReturn,
        Double efficiencyRatio
) {

}
