package com.harsha.bff_service.application.api.service;

import com.harsha.bff_service.application.api.client.InvestmentIntelligenceClient;
import com.harsha.bff_service.application.api.client.MarketIntelligenceClient;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.contracts.dto.marketintelligence.StockLookupResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    private final InvestmentIntelligenceClient investmentIntelligenceClient;
    private final MarketIntelligenceClient marketIntelligenceClient;

    public StockClientService(
            InvestmentIntelligenceClient investmentIntelligenceClient,
            MarketIntelligenceClient marketIntelligenceClient
    ) {
        this.investmentIntelligenceClient = investmentIntelligenceClient;
        this.marketIntelligenceClient = marketIntelligenceClient;
    }

    public StockOverviewResponse getStockOverview(
            String symbol
    ) {
        return investmentIntelligenceClient.getStockOverview(symbol);
    }

    public ResponseEntity<String> getInvInsights(
            String symbol,
            Pageable pageable
    ) {
        return investmentIntelligenceClient.getInvInsights(symbol, pageable);
    }

    public PriceHistoryResponse getPriceHistory(
            String symbol,
            int days
    ) {
        return investmentIntelligenceClient.getPriceHistory(
                symbol,
                days
        );
    }

    public ResponseEntity<String> getMarketInsights(
            String symbol,
            Pageable pageable
    ) {
        return investmentIntelligenceClient.getMarketInsights(
                symbol,
                pageable
        );
    }

    public NarrativeIntelligenceResponse getNarrativeIntelligence(
            String symbol
    ) {
        return marketIntelligenceClient.getNarrativeIntelligence(
                symbol
        );
    }

    public StockLookupResponse getStockLookup() {
        return marketIntelligenceClient.getStockLookup();
    }
}
