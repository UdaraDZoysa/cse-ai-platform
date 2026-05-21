package com.harsha.strategy_service.application.orchestrator;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.application.detector.BreakoutDetector;
import com.harsha.strategy_service.application.detector.MomentumDetector;
import com.harsha.strategy_service.application.detector.TrendDetector;
import com.harsha.strategy_service.application.detector.VolatilityDetector;
import com.harsha.strategy_service.application.evaluator.ConfidenceEngine;
import com.harsha.strategy_service.application.evaluator.SignalFusionEngine;
import com.harsha.strategy_service.application.lifecycle.OpportunityLifecycleManager;
import com.harsha.strategy_service.domain.model.DetectorSignal;
import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.domain.model.SignalDirection;
import com.harsha.strategy_service.domain.repository.OpportunityStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StrategyOrchestrator {
    private final OpportunityStateRepository repository;
    private final TrendDetector trendDetector;
    private final BreakoutDetector breakoutDetector;
    private final MomentumDetector momentumDetector;
    private final VolatilityDetector volatilityDetector;
    private final SignalFusionEngine fusionEngine;
    private final ConfidenceEngine confidenceEngine;
    private final OpportunityLifecycleManager lifecycleManager;

    public StrategyOrchestrator(
            OpportunityStateRepository repository,
            TrendDetector trendDetector,
            BreakoutDetector breakoutDetector,
            MomentumDetector momentumDetector,
            VolatilityDetector volatilityDetector,
            SignalFusionEngine fusionEngine,
            ConfidenceEngine confidenceEngine,
            OpportunityLifecycleManager lifecycleManager
    ) {
        this.repository = repository;
        this.trendDetector = trendDetector;
        this.breakoutDetector = breakoutDetector;
        this.momentumDetector = momentumDetector;
        this.volatilityDetector = volatilityDetector;
        this.fusionEngine = fusionEngine;
        this.confidenceEngine = confidenceEngine;
        this.lifecycleManager = lifecycleManager;
    }

    public void process(
            StockFeatureEvent event
    ) {
        OpportunityState state = repository
                .findBySymbol(event.symbol())
                .orElseGet(() ->
                        new OpportunityState(
                                event.symbol()
                        )
                );

        List<DetectorSignal> signals =
                List.of(
                        trendDetector.detect(event),
                        momentumDetector.detect(event),
                        breakoutDetector.detect(event),
                        volatilityDetector.detect(event)
                );

        double confidence =
                fusionEngine.calculateConfidence(signals);

        SignalDirection direction = fusionEngine.determineDirection(signals);

        confidenceEngine.update(state, confidence);

        state.setDirection(direction);

        lifecycleManager.evaluate(state);

        repository.save(state);

        log.info(
                """
                Strategy evaluation completed.
                symbol={}
                confidence={}
                direction={}
                status={}
                persistence={}
                """,
                state.getSymbol(),
                state.getConfidence(),
                state.getDirection(),
                state.getStatus(),
                state.getPersistenceCount()
        );
    }
}
