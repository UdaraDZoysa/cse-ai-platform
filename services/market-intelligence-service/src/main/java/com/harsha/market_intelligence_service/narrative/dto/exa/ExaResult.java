package com.harsha.market_intelligence_service.narrative.dto.exa;

import java.util.List;

public record ExaResult(
        String title,
        String url,
        String publishedDate,
        List<String> highlights
) {
}
