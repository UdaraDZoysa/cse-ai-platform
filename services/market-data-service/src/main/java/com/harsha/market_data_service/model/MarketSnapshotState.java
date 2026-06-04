package com.harsha.market_data_service.model;

public record MarketSnapshotState(
        double price,
        double percentageChange,
        long shareVolume,
        long tradeVolume,
        double turnover,
        double marketCap,
        double high,
        double low
) {
}
