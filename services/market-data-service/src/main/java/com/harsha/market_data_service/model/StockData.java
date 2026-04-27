package com.harsha.market_data_service.model;

public record StockData(
        String symbol,
        double price,
        double change,
        long shareVolume,
        long lastTradedTime
) {}
