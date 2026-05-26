package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.narrative.agent.NarrativeAgentService;
import com.harsha.market_intelligence_service.narrative.cache.NarrativeCacheService;
import com.harsha.market_intelligence_service.narrative.entity.NarrativeIntelligence;
import com.harsha.market_intelligence_service.narrative.repositoryy.NarrativeIntelligenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NarrativeIntelligenceService {
    private final NarrativeCacheService cacheService;
    private final NarrativeAgentService agentService;
    private final NarrativeIntelligenceRepository repository;
    private static final Logger log = LoggerFactory.getLogger(NarrativeIntelligenceService.class);
    private final Set<String> refreshingSymbols = ConcurrentHashMap.newKeySet();

    public NarrativeIntelligenceService(
            NarrativeCacheService cacheService,
            NarrativeAgentService agentService,
            NarrativeIntelligenceRepository repository) {
        this.cacheService = cacheService;
        this.agentService = agentService;
        this.repository = repository;
    }

    public void refreshIfNeeded(String symbol) {
        if (!refreshingSymbols.add(symbol)) {
            log.info("Narrative refresh already in progress for {}", symbol);
            return;
        }

        try {
            if (!cacheService.isStale(symbol)) {
                log.info("Narrative cache still fresh for {}", symbol);
                return;
            }

            log.info("Refreshing narrative intelligence for {}", symbol);

            String summary = agentService.searchAndSummarize(symbol);

            NarrativeIntelligence intelligence =
                    repository
                            .findBySymbol(symbol)
                            .orElse(
                                    NarrativeIntelligence
                                            .builder()
                                            .symbol(symbol)
                                            .build()
                            );

            intelligence.setSummary(summary);

            intelligence.setRawSearchResult(summary);

            intelligence.setGeneratedAt(
                    Instant.now()
            );

            intelligence.setExpiresAt(
                    Instant.now().plusSeconds(43200)
            );

            repository.save(intelligence);

        } finally {

            refreshingSymbols.remove(symbol);
        }
    }
}
