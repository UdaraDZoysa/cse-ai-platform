package com.harsha.analysis_service.application.service.feature.model;

import com.harsha.contracts.events.analysis.TrendFeatures;
import com.harsha.contracts.events.analysis.MomentumFeatures;
import com.harsha.contracts.events.analysis.VolatilityFeatures;
import com.harsha.contracts.events.analysis.MovingAverageFeatures;

public record StockFeatureSnapshot(
        String symbol,
        long occurredAt,
        TrendFeatures trend,
        MomentumFeatures momentum,
        VolatilityFeatures volatility,
        MovingAverageFeatures movingAverage
) {
}
