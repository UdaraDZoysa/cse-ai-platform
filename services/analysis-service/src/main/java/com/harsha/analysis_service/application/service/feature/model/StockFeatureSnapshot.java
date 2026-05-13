package com.harsha.analysis_service.application.service.feature.model;

import com.harsha.events.market.TrendFeatures;
import com.harsha.events.market.MomentumFeatures;
import com.harsha.events.market.VolatilityFeatures;
import com.harsha.events.market.MovingAverageFeatures;

public record StockFeatureSnapshot(
        String symbol,
        long occurredAt,
        TrendFeatures trend,
        MomentumFeatures momentum,
        VolatilityFeatures volatility,
        MovingAverageFeatures movingAverage
) {
}
