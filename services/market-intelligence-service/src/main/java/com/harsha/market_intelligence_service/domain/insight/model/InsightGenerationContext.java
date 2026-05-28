package com.harsha.market_intelligence_service.domain.insight.model;

import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeSource;

import java.util.List;

public record InsightGenerationContext(
        String symbol,
        String company,
        NarrativeIntelligence narrative,
        List<NarrativeSource> sources
) {
}
