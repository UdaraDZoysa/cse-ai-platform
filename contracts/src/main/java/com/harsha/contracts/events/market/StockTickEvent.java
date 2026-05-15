package com.harsha.contracts.events.market;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

public record StockTickEvent(
        String symbol,
        long occurredAt,
        double price,
        double change,
        long volume,
        double high,
        double low,
        long lastTradedTime
) implements DomainEvent {

    @Override
    public EventType eventType() {
        return EventType.STOCK_TICK_EVENT;
    }
}
