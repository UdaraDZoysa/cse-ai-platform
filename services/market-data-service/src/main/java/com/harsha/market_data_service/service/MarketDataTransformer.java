package com.harsha.market_data_service.service;

import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.market_data_service.filter.StockFilter;
import com.harsha.market_data_service.model.TradeSummaryResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MarketDataTransformer {
    private final StockFilter stockFilter;

    public MarketDataTransformer(StockFilter stockFilter) {
        this.stockFilter = stockFilter;
    }

    public List<StockTickEvent> toEvents(TradeSummaryResponse response) {

        if (response == null || response.reqTradeSummery() == null) {
            return List.of();
        }

        return response.reqTradeSummery()
                .stream()
                .filter(s -> stockFilter.isWatched(s.symbol()))
                .map(s -> new StockTickEvent(
                        s.symbol(),
                        Instant.now().toEpochMilli(),
                        s.price(),
                        s.change(),
                        s.shareVolume(),
                        s.high(),
                        s.low(),
                        s.lastTradedTime()
                ))
                .toList();
    }
}
