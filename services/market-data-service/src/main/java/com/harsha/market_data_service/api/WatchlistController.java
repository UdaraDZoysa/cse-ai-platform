package com.harsha.market_data_service.api;

import com.harsha.market_data_service.filter.StockFilter;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    private final StockFilter stockFilter;

    public WatchlistController(StockFilter stockFilter) {
        this.stockFilter = stockFilter;
    }

    @PostMapping
    public void update(@RequestBody Set<String> symbols) {
        stockFilter.handleWatchlist(symbols);
    }

    @GetMapping
    public Set<String> get() {
        return stockFilter.getWatchlist();
    }
}
