package com.harsha.bff_service.application.api.service;

import com.harsha.bff_service.application.api.client.InvestmentIntelligenceClient;
import com.harsha.bff_service.application.api.client.MarketDataClient;
import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WatchlistService {
    private final MarketDataClient marketDataClient;
    private final InvestmentIntelligenceClient invIntelligenceClient;

    public WatchlistService(
            MarketDataClient marketDataClient,
            InvestmentIntelligenceClient invIntelligenceClient
    ) {
        this.marketDataClient = marketDataClient;
        this.invIntelligenceClient = invIntelligenceClient;
    }

    public void updateWatchlist(
            Set<String> symbols
    ) {
        marketDataClient.updateWatchlist(symbols);
    }

    public WatchlistResponse getWatchlist() {
        return invIntelligenceClient.getWatchlist();
    }
}
