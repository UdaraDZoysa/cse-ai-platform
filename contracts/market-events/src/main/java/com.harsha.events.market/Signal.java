package com.harsha.events.market;

public record Signal(
        String symbol,
        String type,   // TREND / BREAKOUT
        String direction,
        StockFeatures features
) {}
