package com.harsha.frontend_bff_service.application.api.service;

import com.harsha.contracts.dto.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.stock.StockOverviewResponse;
import com.harsha.frontend_bff_service.application.api.client.InvestmentIntelligenceClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {
    private final InvestmentIntelligenceClient investmentIntelligenceClient;

    public StockClientService(
            InvestmentIntelligenceClient investmentIntelligenceClient
    ) {
        this.investmentIntelligenceClient = investmentIntelligenceClient;
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
}
