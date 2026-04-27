package com.harsha.market_data_service.collector;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MarketDataCollector {
    private final WebClient webClient;

    public MarketDataCollector(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://www.cse.lk").build();
    }

    public String fetchRawData() {
        return webClient.post()
                .uri("/api/tradeSummary")
                .header("Content-Type", "application/json")
                .header("Origin", "https://www.cse.lk")
                .header("Referer", "https://www.cse.lk/")
                .bodyValue("{}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
