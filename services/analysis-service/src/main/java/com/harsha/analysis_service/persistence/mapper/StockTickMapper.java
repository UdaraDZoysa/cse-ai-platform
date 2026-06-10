package com.harsha.analysis_service.persistence.mapper;

import com.harsha.analysis_service.persistence.entity.StockTickEntity;
import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StockTickMapper {
    public StockTickEntity toEntity(
            StockTickEvent stockTickEvent
    ) {
        return StockTickEntity.builder()
                .symbol(stockTickEvent.symbol())
                .occurredAt(stockTickEvent.occurredAt())
                .price(stockTickEvent.price())
                .change(stockTickEvent.change())
                .volume(stockTickEvent.volume())
                .high(stockTickEvent.high())
                .low(stockTickEvent.low())
                .lastTradedTime(stockTickEvent.lastTradedTime())
                .build();
    }

    public StockTickEvent toDomain(
            StockTickEntity entity
    ) {
        return new StockTickEvent(
                entity.getSymbol(),
                entity.getOccurredAt(),
                entity.getPrice(),
                entity.getChange(),
                entity.getVolume(),
                entity.getHigh(),
                entity.getLow(),
                entity.getLastTradedTime()
        );
    }
}
