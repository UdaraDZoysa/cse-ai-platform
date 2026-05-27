package com.harsha.market_intelligence_service.narrative.dto.exa;

import java.util.List;

public record ExaSearchResponse(
        Output output,
        List<ExaResult> results
) {
    public record Output(
            ExaStructuredOutput content
    ) {
    }
}
