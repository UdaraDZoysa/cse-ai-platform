package com.harsha.market_intelligence_service.application.api.repository;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;

public interface NarrativeReadRepository {
    NarrativeIntelligenceResponse getNarrativeIntelligence(
            String symbol
    );

    MarketNarrativeDetailsResponse getMarketNarrativeDetails(
            Long id
    );
}
