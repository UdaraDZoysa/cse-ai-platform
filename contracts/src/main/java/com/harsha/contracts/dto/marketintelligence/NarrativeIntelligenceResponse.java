package com.harsha.contracts.dto.marketintelligence;

import java.time.Instant;

public record NarrativeIntelligenceResponse(
        String id,
        String symbol,
        String companyName,
        String summary,
        Instant generatedAt
) {
}
