package com.harsha.market_intelligence_service.application.api.mapper;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeSource;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeSource;
import org.springframework.stereotype.Component;

@Component
public class NarrativeSourceMapper {
    public NarrativeIntelligenceResponse toIntelligence(
            NarrativeIntelligence intelligence
    ) {
        return new NarrativeIntelligenceResponse(
                intelligence.getId().toString(),
                intelligence.getSymbol(),
                intelligence.getCompany(),
                intelligence.getSummary(),
                intelligence.getGeneratedAt()
        );
    }

    public MarketNarrativeSource toMarketNarrativeSource(
            NarrativeSource narrativeSource
    ) {
        return new MarketNarrativeSource(
                narrativeSource.getIntelligence().getSymbol(),
                narrativeSource.getIntelligence().getCompany(),
                narrativeSource.getTitle(),
                narrativeSource.getSourceUrl(),
                narrativeSource.getPublishedDate()
        );
    }
}
