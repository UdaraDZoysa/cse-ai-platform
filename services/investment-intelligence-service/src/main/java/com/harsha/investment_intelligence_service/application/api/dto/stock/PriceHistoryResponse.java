package com.harsha.investment_intelligence_service.application.api.dto.stock;

import java.util.List;

public record PriceHistoryResponse(
        List<PriceHistoryPoint> points
) {
}
