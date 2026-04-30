package com.harsha.events.market;

public record StockFeatures(
        String symbol,
        String trend,
        int trendStrength,
        double priceChangePercent,
        double volatility,
        boolean volumeSpike,
        double price,
        double high,
        double low,
        double change
) {}
