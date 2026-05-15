package com.harsha.contracts.events.market;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

public record StockFeatureEvent(
        String symbol,
        long occurredAt,
        TrendFeatures trend,
        MomentumFeatures momentum,
        VolatilityFeatures volatility,
        MovingAverageFeatures movingAverage
) implements DomainEvent {

    @Override
    public EventType eventType() {
        return EventType.STOCK_FEATURE_EVENT;
    }
}
