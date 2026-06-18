package com.harsha.bff_service.application.api.client;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.contracts.dto.marketintelligence.StockLookupResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MarketIntelligenceClient {
    private final RestClient restClient;

    public MarketIntelligenceClient(
            RestClient.Builder builder,
            @Value("${market-intelligence-url}")
            String baseUrl
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    //////////////////////////////////////////////////////////////////////////
    /// STOCK END POINTS
    /////////////////////////////////////////////////////////////////////////

    public NarrativeIntelligenceResponse getNarrativeIntelligence(
            String symbol
    ) {
        return restClient.get()
                .uri("/api/market-narrative/{symbol}", symbol)
                .retrieve()
                .body(NarrativeIntelligenceResponse.class);
    }

    public MarketNarrativeDetailsResponse getNarrativeDetails(
            String id
    ) {
        return restClient.get()
                .uri("/api/market-narrative/details/{id}", id)
                .retrieve()
                .body(MarketNarrativeDetailsResponse.class);
    }

    public StockLookupResponse getStockLookup() {
        return restClient.get()
                .uri("/api/stock-lookup")
                .retrieve()
                .body(StockLookupResponse.class);
    }
}
