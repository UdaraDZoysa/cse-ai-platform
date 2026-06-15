package com.harsha.contracts.dto.stock;

import java.time.Instant;

public record PriceHistoryPoint(
        Instant timestamp,
        double price
) {
}
