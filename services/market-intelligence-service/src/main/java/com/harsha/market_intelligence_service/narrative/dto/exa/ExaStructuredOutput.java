package com.harsha.market_intelligence_service.narrative.dto.exa;

import java.util.List;

public record ExaStructuredOutput(
        String summary,
        List<String> majorDevelopments,
        List<String> strategicMoves,
        List<String> risks
) {
}
