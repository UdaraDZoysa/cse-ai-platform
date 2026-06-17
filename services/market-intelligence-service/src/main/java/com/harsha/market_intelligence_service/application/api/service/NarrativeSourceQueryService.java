package com.harsha.market_intelligence_service.application.api.service;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.market_intelligence_service.application.api.repository.NarrativeReadRepository;
import org.springframework.stereotype.Service;

@Service
public class NarrativeSourceQueryService {
    private final NarrativeReadRepository sourceReadRepository;

    public NarrativeSourceQueryService(
            NarrativeReadRepository sourceReadRepository
    ) {
        this.sourceReadRepository = sourceReadRepository;
    }

    public NarrativeIntelligenceResponse getNarrativeIntelligence(
            String symbol
    ) {
        return sourceReadRepository.getNarrativeIntelligence(symbol);
    }

    public MarketNarrativeDetailsResponse getMarketNarrativeDetails(
            Long id
    ) {
        return sourceReadRepository.getMarketNarrativeDetails(id);
    }
}
