package com.harsha.market_intelligence_service.application.api.adapter;

import com.harsha.contracts.dto.marketintelligence.MarketNarrativeDetailsResponse;
import com.harsha.contracts.dto.marketintelligence.NarrativeIntelligenceResponse;
import com.harsha.market_intelligence_service.application.api.mapper.NarrativeSourceMapper;
import com.harsha.market_intelligence_service.application.api.repository.NarrativeReadRepository;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeSourceRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class NarrativeReadRepositoryAdapter
        implements NarrativeReadRepository {

    private final NarrativeSourceRepository sourceRepository;
    private final NarrativeIntelligenceRepository intelligenceRepository;
    private final NarrativeSourceMapper mapper;

    public NarrativeReadRepositoryAdapter(
            NarrativeSourceRepository sourceRepository,
            NarrativeIntelligenceRepository intelligenceRepository,
            NarrativeSourceMapper mapper
    ) {
        this.sourceRepository = sourceRepository;
        this.intelligenceRepository = intelligenceRepository;
        this.mapper = mapper;
    }

    @Override
    public NarrativeIntelligenceResponse getNarrativeIntelligence(String symbol) {
        return intelligenceRepository
                .findBySymbol(symbol)
                .map(mapper::toIntelligence)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Intelligence Not Found"
                        )
                );
    }

    @Override
    public MarketNarrativeDetailsResponse getMarketNarrativeDetails(Long id) {
        return new MarketNarrativeDetailsResponse(
                sourceRepository
                        .findByIntelligence_Id(id)
                        .stream()
                        .map(mapper::toMarketNarrativeSource)
                        .toList()
        );
    }
}
