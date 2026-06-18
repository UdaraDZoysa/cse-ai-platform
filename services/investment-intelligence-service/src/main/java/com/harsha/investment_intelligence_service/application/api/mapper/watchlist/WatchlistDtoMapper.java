package com.harsha.investment_intelligence_service.application.api.mapper.watchlist;

import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.WatchlistEntity;
import org.springframework.stereotype.Component;

@Component
public class WatchlistDtoMapper {
    public WatchlistResponse toWatchlist(
            WatchlistEntity entity
    ) {
        return new WatchlistResponse(
                entity.getWatchlistId(),
                entity.getSymbols(),
                entity.getUpdatedAt()
        );
    }
}
