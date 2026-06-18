package com.harsha.investment_intelligence_service.application.api.service;

import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import com.harsha.investment_intelligence_service.application.api.repository.watchlist.WatchlistReadRepository;
import org.springframework.stereotype.Service;

@Service
public class WatchlistQueryService {
    private final WatchlistReadRepository watchlistReadRepository;

    public WatchlistQueryService(
            WatchlistReadRepository watchlistReadRepository
    ) {
        this.watchlistReadRepository = watchlistReadRepository;
    }

    public WatchlistResponse getWatchlist() {
        return watchlistReadRepository.getWatchlist();
    }
}
