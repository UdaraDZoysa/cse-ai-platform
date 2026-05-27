package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class NarrativeIntelligencePersistenceService {
    private final NarrativeIntelligenceRepository repository;
    private final NarrativeSourcePersistenceService sourcePersistenceService;

    public NarrativeIntelligencePersistenceService(
            NarrativeIntelligenceRepository repository,
            NarrativeSourcePersistenceService sourcePersistenceService) {
        this.repository = repository;
        this.sourcePersistenceService = sourcePersistenceService;
    }

    @Transactional
    public void persistIntelligence(
            String symbol,
            NarrativeExtractionResult result
    ) {

        NarrativeIntelligence intelligence =
                repository
                        .findBySymbol(symbol)
                        .orElse(
                                NarrativeIntelligence
                                        .builder()
                                        .symbol(symbol)
                                        .build()
                        );

        intelligence.setSummary(
                result.summary()
        );

        intelligence.setGeneratedAt(
                Instant.now()
        );

        intelligence.setExpiresAt(
                Instant.now().plusSeconds(43200)
        );

        NarrativeIntelligence saved =
                repository.save(intelligence);

        sourcePersistenceService.persistSources(
                saved,
                result.sources()
        );
    }
}
