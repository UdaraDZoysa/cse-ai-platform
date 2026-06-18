package com.harsha.bff_service.application.api.client;

import com.harsha.contracts.dto.invintelligence.invinsight.InvestmentInsightDetailResponse;
import com.harsha.contracts.dto.invintelligence.marketinsight.MarketInsightDetailResponse;
import com.harsha.contracts.dto.invintelligence.stock.PriceHistoryResponse;
import com.harsha.contracts.dto.invintelligence.stock.StockOverviewResponse;
import com.harsha.contracts.dto.invintelligence.watchlist.WatchlistResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.data.domain.Pageable;

@Component
public class InvestmentIntelligenceClient {
    private final RestClient restClient;

    public InvestmentIntelligenceClient(
            RestClient.Builder builder,
            @Value("${inv-intelligence-url}")
            String invIntelligenceUrl
    ) {
        this.restClient = builder
                .baseUrl(invIntelligenceUrl)
                .build();
    }

    //////////////////////////////////////////////////////////////////////////
    /// STOCK END POINTS
    /////////////////////////////////////////////////////////////////////////

    public StockOverviewResponse getStockOverview(
            String symbol
    ) {
        return restClient.get()
                .uri("/api/stocks/{symbol}/overview", symbol)
                .retrieve()
                .body(StockOverviewResponse.class);
    }

    public ResponseEntity<String> getInvInsights(
            String symbol,
            Pageable pageable
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/{symbol}/investment-insights")
                        .queryParam(
                                "page",
                                pageable.getPageNumber()
                        )
                        .queryParam(
                                "size",
                                pageable.getPageSize()
                        )
                        .build(symbol))
                .retrieve()
                .toEntity(String.class);
    }

    public PriceHistoryResponse getPriceHistory(
            String symbol,
            int days
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/{symbol}/price-history")
                        .queryParam(
                                "days",
                                days
                        )
                        .build(symbol))
                .retrieve()
                .body(PriceHistoryResponse.class);

    }

    public ResponseEntity<String> getMarketInsights(
            String symbol,
            Pageable pageable
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stocks/{symbol}/market-insights")
                        .queryParam(
                                "page",
                                pageable.getPageNumber()
                        )
                        .queryParam(
                                "size",
                                pageable.getPageSize()
                        )
                        .build(symbol))
                .retrieve()
                .toEntity(String.class);
    }

    //////////////////////////////////////////////////////////////////////////
    /// INV_INSIGHT END POINTS
    /////////////////////////////////////////////////////////////////////////

    public ResponseEntity<String> getInvInsights(
            Pageable pageable
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/investment-insights")
                        .queryParam(
                                "page",
                                pageable.getPageNumber()
                        ).queryParam(
                                "size",
                                pageable.getPageSize()
                        )
                        .build())
                .retrieve()
                .toEntity(String.class);
    }

    public InvestmentInsightDetailResponse getInvInsight(
            String id
    ) {
        return restClient.get()
                .uri("/api/investment-insights/{id}", id)
                .retrieve()
                .body(InvestmentInsightDetailResponse.class);
    }

    //////////////////////////////////////////////////////////////////////////
    /// MARKET_INSIGHT END POINTS
    /////////////////////////////////////////////////////////////////////////

    public MarketInsightDetailResponse getMarketInsight(
            String id
    ) {
        return restClient.get()
                .uri("/api/market-insight/{id}", id)
                .retrieve()
                .body(MarketInsightDetailResponse.class);
    }

    //////////////////////////////////////////////////////////////////////////
    /// WATCHLIST END POINTS
    /////////////////////////////////////////////////////////////////////////

    public WatchlistResponse getWatchlist() {
        return restClient.get()
                .uri("/api/watchlist")
                .retrieve()
                .body(WatchlistResponse.class);
    }
}
