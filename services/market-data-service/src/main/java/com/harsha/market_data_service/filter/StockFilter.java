package com.harsha.market_data_service.filter;

import com.harsha.contracts.events.common.WatchlistUpdatedEvent;
import com.harsha.market_data_service.publisher.WatchListPublisher;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockFilter {
    private final WatchListPublisher publisher;
    private final Set<String> watchlist = ConcurrentHashMap.newKeySet();

    public StockFilter(
            WatchListPublisher publisher) {
        this.publisher = publisher;
    }

    //DON’T process anything if watchList empty
    public boolean isWatched(String symbol) {
        return !watchlist.isEmpty() && watchlist.contains(symbol);
    }

    public void handleWatchlist(Set<String> symbols) {
        watchlist.clear();
        watchlist.addAll(symbols);

        //Publish Watchlist
        publisher.publish(
                new WatchlistUpdatedEvent(
                        UUID.randomUUID().toString(),
                        symbols,
                        Instant.now()
                )
        );
    }

    public Set<String> getWatchlist() {
        return watchlist;
    }
}
