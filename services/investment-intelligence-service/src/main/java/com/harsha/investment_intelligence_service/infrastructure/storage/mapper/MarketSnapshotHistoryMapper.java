package com.harsha.investment_intelligence_service.infrastructure.storage.mapper;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketSnapshotHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class MarketSnapshotHistoryMapper {
    public MarketSnapshotHistoryEntity toEntity(
            MarketSnapshotEvent event
    ) {
        return MarketSnapshotHistoryEntity.builder()
                .symbol(event.symbol())
                .occurredAt(event.occurredAt())
                .price(event.price())
                .percentageChange(event.percentageChange())
                .previousClose(event.previousClose())
                .open(event.open())
                .high(event.high())
                .low(event.low())
                .shareVolume(event.shareVolume())
                .turnover(event.turnover())
                .marketCap(event.marketCap())
                .lastTradedTime(event.lastTradedTime())
                .build();
    }

    public MarketSnapshotEvent toDomain(
            MarketSnapshotHistoryEntity entity
    ) {
        return new MarketSnapshotEvent(
                entity.getSymbol(),
                entity.getOccurredAt(),
                entity.getPrice(),
                entity.getPercentageChange(),
                entity.getPreviousClose(),
                entity.getOpen(),
                entity.getHigh(),
                entity.getLow(),
                entity.getShareVolume(),
                entity.getTradeVolume(),
                entity.getTurnover(),
                entity.getMarketCap(),
                entity.getLastTradedTime()
        );
    }
}
