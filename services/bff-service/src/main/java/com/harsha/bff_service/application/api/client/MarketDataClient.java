package com.harsha.bff_service.application.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Set;

@Component
public class MarketDataClient {
    private final RestClient restClient;

    public MarketDataClient(
            RestClient.Builder builder,
            @Value("${market-data-url}")
            String marketDataUrl
    ) {
        this.restClient = builder
                .baseUrl(marketDataUrl)
                .build();
    }

    public void updateWatchlist(
            Set<String> symbols
    ) {
        restClient.post()
                .uri("/api/watchlist")
                .body(symbols)
                .retrieve()
                .toBodilessEntity();
    }

    public Set<String> getWatchlist() {
        return restClient.get()
                .uri("api/watchlist")
                .retrieve()
                .body(
                        new ParameterizedTypeReference<Set<String>>() {

                        }
                );
    }
}
