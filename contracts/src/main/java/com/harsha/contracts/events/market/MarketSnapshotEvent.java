package com.harsha.contracts.events.market;

import com.harsha.contracts.events.DomainEvent;
import com.harsha.contracts.messaging.EventType;

public record MarketSnapshotEvent(
        String symbol,
        String company,
        long occurredAt,
        double price,
        double percentageChange,
        double previousClose,
        double open,
        double high,
        double low,
        long shareVolume,
        long tradeVolume,
        double turnover,
        double marketCap,
        long lastTradedTime
) implements DomainEvent {
    @Override
    public EventType eventType() {
        return EventType.MARKET_SNAPSHOT_EVENT;
    }
}
