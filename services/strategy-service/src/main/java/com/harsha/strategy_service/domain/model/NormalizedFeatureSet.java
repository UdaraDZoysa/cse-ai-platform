package com.harsha.strategy_service.domain.model;

public record NormalizedFeatureSet(
        double returnZScore,
        double volatilityZScore,
        double momentumZScore
) {
}
