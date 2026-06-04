package com.harsha.market_intelligence_service.application.insight.orchestrator;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.market_intelligence_service.application.events.EventPublisher;
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
    private final EventPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(MarketInsightOrchestrator.class);

    public MarketInsightOrchestrator(
            NarrativeAggregationService aggregationService,
            MarketInsightGenerationService generationService,
            MarketInsightPersistenceService persistenceService,
            EventPublisher publisher
    ) {
        this.aggregationService = aggregationService;
        this.generationService = generationService;
        this.persistenceService = persistenceService;
        this.publisher = publisher;
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

        MarketInsight savedInsight =
                persistenceService.save(insight);

        MarketInsightGeneratedEvent insightGeneratedEvent =
                new MarketInsightGeneratedEvent(
                        savedInsight.getSymbol(),
                        savedInsight.getGeneratedAt().toEpochMilli(),
                        savedInsight.getCompany(),
                        savedInsight.getSummary(),
                        savedInsight.getReasoning(),
                        savedInsight.getSentiment(),
                        savedInsight.getImportanceScore(),
                        savedInsight.getConfidenceScore(),
                        savedInsight.getPersistenceScore(),
                        savedInsight.getExpiresAt().toEpochMilli(),
                        savedInsight.getGeneratedBy()
                );

        publisher.publish(
                symbol,
                EventType.MARKET_INSIGHT_GENERATED_EVENT,
                insightGeneratedEvent
        );

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
