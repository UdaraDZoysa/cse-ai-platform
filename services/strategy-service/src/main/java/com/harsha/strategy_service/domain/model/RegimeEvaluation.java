package com.harsha.strategy_service.domain.model;

public record RegimeEvaluation(
        RegimeState regimeState,
        double trendWeight,
        double momentumWeight,
        double breakoutWeight,
        double volatilityWeight
) {
}
