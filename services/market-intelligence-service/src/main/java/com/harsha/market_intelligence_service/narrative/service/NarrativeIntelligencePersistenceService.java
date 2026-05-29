package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.application.insight.service.InsightGenJobPersistenceService;
import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import com.harsha.market_intelligence_service.messaging.publisher.InsightGeneration.InsightGenJobProcessor;
import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;

@Service
public class NarrativeIntelligencePersistenceService {
    private final NarrativeIntelligenceRepository repository;
    private final NarrativeSourcePersistenceService sourcePersistenceService;
    private final InsightGenJobPersistenceService insightGenJobPersistenceService;
    private final InsightGenJobProcessor insightGenJobProcessor;
    private static final Logger log = LoggerFactory.getLogger(NarrativeIntelligencePersistenceService.class);

    public NarrativeIntelligencePersistenceService(
            NarrativeIntelligenceRepository repository,
            NarrativeSourcePersistenceService sourcePersistenceService,
            InsightGenJobPersistenceService insightGenJobPersistenceService,
            InsightGenJobProcessor insightGenJobProcessor) {
        this.repository = repository;
        this.sourcePersistenceService = sourcePersistenceService;
        this.insightGenJobPersistenceService = insightGenJobPersistenceService;
        this.insightGenJobProcessor = insightGenJobProcessor;
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
                        try {
                            InsightGenerationJob saved =
                                    insightGenJobPersistenceService
                                            .persistInsightGenJob(symbol);
                            if(saved != null) {
                                insightGenJobProcessor.startInsightGenJob();
                            }
                        } catch (Exception ex) {
                            log.debug("Error persisting insight gen job", ex);
                        }
                    }
                }
        );
    }
}
