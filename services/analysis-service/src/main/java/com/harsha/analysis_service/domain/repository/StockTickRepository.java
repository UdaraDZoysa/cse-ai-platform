package com.harsha.analysis_service.domain.repository;

import com.harsha.contracts.events.market.StockTickEvent;

import java.util.List;

public interface StockTickRepository {
    void save(
            StockTickEvent event
    );

    List<StockTickEvent> findLatestBySymbol(
            String symbol,
            int limit
    );

    List<String> findTrackedSymbols();

    int deleteOlderThan(long cutoff);

}
