package com.harsha.investment_intelligence_service.application.api.repository.watchlist;

import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;

public interface WatchlistReadRepository {
    WatchlistResponse getWatchlist();
}
