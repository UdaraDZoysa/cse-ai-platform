package com.harsha.market_intelligence_service.application.insight.orchestrator;

import com.harsha.market_intelligence_service.application.insight.service.MarketInsightGenerationService;
import com.harsha.market_intelligence_service.application.insight.service.NarrativeAggregationService;
import com.harsha.market_intelligence_service.application.insight.service.MarketInsightPersistenceService;
import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.model.InsightGenerationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MarketInsightOrchestrator {
    private final NarrativeAggregationService aggregationService;
    private final MarketInsightGenerationService generationService;
    private final MarketInsightPersistenceService persistenceService;
    private static final Logger log = LoggerFactory.getLogger(MarketInsightOrchestrator.class);

    public MarketInsightOrchestrator(
            NarrativeAggregationService aggregationService,
            MarketInsightGenerationService generationService,
            MarketInsightPersistenceService persistenceService
    ) {
        this.aggregationService = aggregationService;
        this.generationService = generationService;
        this.persistenceService = persistenceService;
    }

    public void generate(
            String symbol
    ) {
        InsightGenerationContext context =
                aggregationService.aggregate(
                        symbol
                );
        MarketInsight insight =
                generationService.generate(
                        context
                );

        persistenceService.save(insight);

        log.info(
                """
                
                Market insight generated.
                
                symbol={}
                sentiment={}
                importance={}
                
                """,

                insight.getSymbol(),
                insight.getSentiment(),
                insight.getImportanceScore()
        );
    }
}
