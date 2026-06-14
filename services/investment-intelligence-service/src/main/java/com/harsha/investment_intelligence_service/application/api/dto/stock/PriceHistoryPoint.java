package com.harsha.investment_intelligence_service.application.api.dto.stock;

import java.time.Instant;

public record PriceHistoryPoint(
        Instant timestamp,
        double price
) {
}
