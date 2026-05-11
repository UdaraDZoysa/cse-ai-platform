package com.harsha.events.market;

public record StockFeatureEvent(
        String symbol,
        long occurredAt,
        TrendFeatures trend,
        MomentumFeatures momentum,
        VolatilityFeatures volatility,
        MovingAverageFeatures movingAverage
) {
}
