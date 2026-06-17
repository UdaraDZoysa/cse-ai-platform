package com.harsha.contracts.dto.invintelligence.stock;

import java.time.Instant;

public record PriceHistoryPoint(
        Instant timestamp,
        double price
) {
}
