package com.harsha.market_intelligence_service.application.insight.trigger;

import com.harsha.market_intelligence_service.application.insight.evaluator.InsightRefreshEvaluator;
import com.harsha.market_intelligence_service.application.insight.orchestrator.MarketInsightOrchestrator;
import com.harsha.market_intelligence_service.domain.insight.entity.MarketInsight;
import com.harsha.market_intelligence_service.domain.insight.model.InsightExecutionResult;
import com.harsha.market_intelligence_service.domain.insight.model.InsightRefreshDecision;
import com.harsha.market_intelligence_service.domain.insight.repository.MarketInsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MarketInsightExecutionService {
    private final MarketInsightRepository repository;
    private final InsightRefreshEvaluator refreshEvaluator;
    private final MarketInsightOrchestrator orchestrator;
    private static final Logger log =
            LoggerFactory.getLogger(MarketInsightExecutionService.class);

    public MarketInsightExecutionService(
            MarketInsightRepository repository,
            InsightRefreshEvaluator refreshEvaluator,
            MarketInsightOrchestrator orchestrator
    ) {
        this.repository = repository;
        this.refreshEvaluator = refreshEvaluator;
        this.orchestrator = orchestrator;
    }

    public InsightExecutionResult trigger(
            String symbol
    ) {
        MarketInsight existing = repository
                .findBySymbol(symbol)
                .orElse(null);

        InsightRefreshDecision decision =
                refreshEvaluator.evaluate(existing);

        log.info(
                """
                
                Insight refresh evaluation completed.
                
                symbol={}
                refresh={}
                reason={}
                
                """,
                symbol,
                decision.refresh(),
                decision.reason()
        );

        if (!decision.refresh()) {
            return InsightExecutionResult.skipped(
                    decision.reason()
            );
        }

        log.info(
                """
                
                Triggering market insight generation.
                
                symbol={}
                
                """,
                symbol
        );

        orchestrator.generate(symbol);

        return InsightExecutionResult.insightGenerated();
    }
}
