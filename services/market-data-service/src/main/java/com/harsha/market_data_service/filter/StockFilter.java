package com.harsha.market_data_service.filter;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockFilter {
    private final Set<String> watchlist = ConcurrentHashMap.newKeySet();

    //DON’T process anything if watchList empty
    public boolean isWatched(String symbol) {
        return !watchlist.isEmpty() && watchlist.contains(symbol);
    }

    public void updateWatchlist(Set<String> symbols) {
        watchlist.clear();
        watchlist.addAll(symbols);
    }

    public Set<String> getWatchlist() {
        return watchlist;
    }
}
