package com.harsha.market_data_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CseStockResponse(
        String symbol,
        double price,
        double change,
        @JsonProperty("sharevolume")
        long shareVolume,
        double high,
        double low,
        long lastTradedTime
) {}
