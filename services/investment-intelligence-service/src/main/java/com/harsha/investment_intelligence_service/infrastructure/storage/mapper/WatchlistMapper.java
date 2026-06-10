package com.harsha.investment_intelligence_service.infrastructure.storage.mapper;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.WatchlistEntity;
import org.springframework.stereotype.Component;

@Component
public class WatchlistMapper {
    public WatchlistEntity toEntity(
            WatchlistUpdatedEvent event
    ) {
        return WatchlistEntity.builder()
                .watchlistId(event.watchlistId())
                .symbols(event.symbols())
                .updatedAt(event.updatedAt())
                .build();
    }

    public WatchlistUpdatedEvent toDomain(
            WatchlistEntity entity
    ) {
        return new WatchlistUpdatedEvent(
                entity.getWatchlistId(),
                entity.getSymbols(),
                entity.getUpdatedAt()
        );
    }
}
