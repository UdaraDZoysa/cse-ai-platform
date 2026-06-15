package com.harsha.contracts.dto.stock;

import java.util.List;

public record PriceHistoryResponse(
        List<PriceHistoryPoint> points
) {
}
