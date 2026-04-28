package com.harsha.market_data_service.model;

public record StockFeatures(
        String symbol,
        String trend,
        int trendStrength,
        double priceChangePercent,
        double volatility,
        boolean volumeSpike,
        double price
) {}
