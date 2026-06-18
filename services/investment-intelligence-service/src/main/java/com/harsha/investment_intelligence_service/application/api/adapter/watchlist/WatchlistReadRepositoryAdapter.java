package com.harsha.investment_intelligence_service.application.api.adapter.watchlist;

import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import com.harsha.investment_intelligence_service.application.api.mapper.watchlist.WatchlistDtoMapper;
import com.harsha.investment_intelligence_service.application.api.repository.watchlist.WatchlistReadRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaWatchlistRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class WatchlistReadRepositoryAdapter
        implements WatchlistReadRepository {

    private final JpaWatchlistRepository watchlistRepository;
    private final WatchlistDtoMapper watchlistMapper;

    public WatchlistReadRepositoryAdapter(
            JpaWatchlistRepository watchlistRepository,
            WatchlistDtoMapper watchlistMapper
    ) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistMapper = watchlistMapper;
    }

    @Override
    public WatchlistResponse getWatchlist() {
        return watchlistRepository
                .findTopByOrderByUpdatedAtDesc()
                .map(watchlistMapper::toWatchlist)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Watchlist not found"
                        )
                );
    }
}
