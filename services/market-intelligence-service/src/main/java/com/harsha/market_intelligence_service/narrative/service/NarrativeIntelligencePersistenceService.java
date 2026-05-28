package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.application.insight.trigger.MarketInsightTriggerService;
import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Component
public class NarrativeIntelligencePersistenceService {
    private final NarrativeIntelligenceRepository repository;
    private final NarrativeSourcePersistenceService sourcePersistenceService;
    private final MarketInsightTriggerService triggerService;

    public NarrativeIntelligencePersistenceService(
            NarrativeIntelligenceRepository repository,
            NarrativeSourcePersistenceService sourcePersistenceService,
            MarketInsightTriggerService triggerService) {
        this.repository = repository;
        this.sourcePersistenceService = sourcePersistenceService;
        this.triggerService = triggerService;
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

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        triggerService.trigger(symbol);
                    }
                }
        );
    }
}
