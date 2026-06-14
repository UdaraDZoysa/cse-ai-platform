package com.harsha.investment_intelligence_service.application.api.dto.stock;

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
