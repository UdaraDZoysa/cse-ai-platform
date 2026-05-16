package com.harsha.analysis_service.application.service.feature.model;

import com.harsha.contracts.events.market.TrendFeatures;
import com.harsha.contracts.events.market.MomentumFeatures;
import com.harsha.contracts.events.market.VolatilityFeatures;
import com.harsha.contracts.events.market.MovingAverageFeatures;

public record StockFeatureSnapshot(
        String symbol,
        long occurredAt,
        TrendFeatures trend,
        MomentumFeatures momentum,
        VolatilityFeatures volatility,
        MovingAverageFeatures movingAverage
) {
}
