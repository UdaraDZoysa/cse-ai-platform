package com.harsha.contracts.dto.invintelligence.stock;

import java.time.Instant;

public record StockOverviewResponse(
        String symbol,
        String companyName,
        double currentPrice,
        double percentageChange,
        double previousClose,
        double open,
        double high,
        double low,
        long shareVolume,
        long tradeVolume,
        double turnover,
        double marketCap,
        Instant lastUpdatedAt
) {
}
