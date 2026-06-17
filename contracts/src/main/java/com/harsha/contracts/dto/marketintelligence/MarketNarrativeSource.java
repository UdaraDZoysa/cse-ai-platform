package com.harsha.contracts.dto.marketintelligence;

import java.time.Instant;

public record MarketNarrativeSource(
        String symbol,
        String companyName,
        String title,
        String sourceUrl,
        Instant publishedDate
) {
}
