package com.harsha.market_intelligence_service.narrative.service;

import com.harsha.market_intelligence_service.masterdata.service.CompanySymbolResolver;
import com.harsha.market_intelligence_service.narrative.cache.NarrativeCacheService;
import com.harsha.market_intelligence_service.narrative.client.WebSearchClient;
import com.harsha.market_intelligence_service.narrative.dto.NarrativeExtractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NarrativeIntelligenceService {
    private final NarrativeCacheService cacheService;
    private final WebSearchClient webSearchClient;
    private final CompanySymbolResolver companySymbolResolver;
    private final NarrativeIntelligencePersistenceService persistIntelligence;
    private static final Logger log = LoggerFactory.getLogger(NarrativeIntelligenceService.class);
    private final Set<String> refreshingSymbols = ConcurrentHashMap.newKeySet();

    public NarrativeIntelligenceService(
            NarrativeCacheService cacheService,
            WebSearchClient webSearchClient,
            CompanySymbolResolver companySymbolResolver,
            NarrativeIntelligencePersistenceService persistIntelligence) {
        this.cacheService = cacheService;
        this.webSearchClient = webSearchClient;
        this.companySymbolResolver = companySymbolResolver;
        this.persistIntelligence = persistIntelligence;
    }

    public void updateNarrativeIntelligence(String symbol) {
        if (!refreshingSymbols.add(symbol)) {
            log.debug("Narrative refresh already in progress for {}", symbol);
            return;
        }

        try {
            String companyName =
                    companySymbolResolver.resolveCompanyName(symbol);

            if (!cacheService.isStale(symbol)) {
                log.debug("Narrative cache still fresh for {} ({})", companyName, symbol);
                return;
            }

            log.info("Refreshing narrative intelligence for {} ({})", companyName, symbol);

            NarrativeExtractionResult result =
                    webSearchClient.search(
                            symbol,
                            companyName
                    );

            if (isEmptyResult(result)) {
                log.warn("No narrative intelligence generated for {} ({})", companyName, symbol);
                return;
            }

            persistIntelligence.persistIntelligence(
                    symbol,
                    result
            );

            log.info("Narrative intelligence refreshed for {} ({})", companyName, symbol);

        } catch (Exception ex) {
            log.error(
                    """
                    Failed to refresh narrative intelligence.
                    symbol={}
                    error={}
                    """,
                    symbol,
                    ex.getMessage(),
                    ex
            );

        } finally {

            refreshingSymbols.remove(symbol);
        }
    }
    private boolean isEmptyResult(
            NarrativeExtractionResult result
    ) {
        return result == null
                || ((result.summary() == null
                        || result.summary().isBlank())
                && (result.sources() == null
                        || result.sources().isEmpty())
        );
    }
}
