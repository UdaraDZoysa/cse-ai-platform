package com.harsha.analysis_service.application.service.evaluator.model;

import com.harsha.analysis_service.application.service.evaluator.MarketRegime;

public record MarketEvaluationResult(
        double significanceScore,
        boolean publish,
        boolean persist,
        MarketRegime marketRegime,
        double confidence
) {}
