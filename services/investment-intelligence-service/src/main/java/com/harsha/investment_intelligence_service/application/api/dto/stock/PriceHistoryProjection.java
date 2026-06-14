package com.harsha.investment_intelligence_service.application.api.dto.stock;

public record PriceHistoryProjection(
        long occurredAt,
        double price
) {
}
