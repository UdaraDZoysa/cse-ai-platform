package com.harsha.market_data_service.diff;

import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketDataDiffEngine {
    private final Map<String, Double> lastPrices = new ConcurrentHashMap<>();

    public boolean hasChanged(StockTickEvent event) {

        Double lastPrice = lastPrices.get(event.symbol());

        if (lastPrice == null || Math.abs(lastPrice - event.price()) > 0.0001) {
            lastPrices.put(event.symbol(), event.price());
            return true;
        }

        return false;
    }
}
