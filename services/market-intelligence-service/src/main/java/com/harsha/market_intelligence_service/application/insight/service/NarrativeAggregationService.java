package com.harsha.market_intelligence_service.application.insight.service;

import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NarrativeAggregationService {
    private final NarrativeIntelligenceRepository repository;

    public NarrativeAggregationService(
            NarrativeIntelligenceRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public InsightGenerationContext aggregate(
            String symbol
    ) {
        NarrativeIntelligence narrative =
                repository.findBySymbolWithSources(symbol)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Narrative not found for symbol: "
                                                + symbol
                                )
                        );

        return new InsightGenerationContext(
                narrative.getSymbol(),
                narrative.getCompany(),
                narrative,
                narrative.getSources()
        );
    }
}
