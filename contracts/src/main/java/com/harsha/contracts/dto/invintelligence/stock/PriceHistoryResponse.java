package com.harsha.contracts.dto.invintelligence.stock;

import java.util.List;

public record PriceHistoryResponse(
        List<PriceHistoryPoint> points
) {
}
