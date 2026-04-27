package com.harsha.events.market;

public record StockTickEvent(
        String symbol,
        double price,
        double change,
        long volume,
        long timestamp
) {}