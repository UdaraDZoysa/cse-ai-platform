package com.harsha.market_data_service.model;

public record Signal(
        String symbol,
        String type,   // TREND / BREAKOUT
        String direction,
        StockFeatures features
) {}
