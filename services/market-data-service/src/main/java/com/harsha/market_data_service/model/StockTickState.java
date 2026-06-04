package com.harsha.market_data_service.model;

public record StockTickState(
        double price,
        long volume,
        double high,
        double low
) {
}
