package com.harsha.strategy_service.application.orchestrator;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.events.strategy.SignalDirection;
import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.strategy_service.application.detector.*;
import com.harsha.strategy_service.application.evaluator.ConfidenceEngine;
import com.harsha.strategy_service.application.evaluator.SignalFusionEngine;
import com.harsha.strategy_service.application.events.EventPublisher;
import com.harsha.strategy_service.application.lifecycle.OpportunityLifecycleManager;
import com.harsha.strategy_service.application.regime.RegimeContext;
import com.harsha.strategy_service.application.regime.RegimeContextService;
import com.harsha.strategy_service.application.statistics.SymbolStatisticsService;
import com.harsha.strategy_service.application.transition.OpportunityTransitionEvaluator;
import com.harsha.strategy_service.domain.model.*;
import com.harsha.strategy_service.domain.model.detector.DetectorSignal;
import com.harsha.strategy_service.domain.model.transition.TransitionResult;
import com.harsha.strategy_service.domain.repository.OpportunityStateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    private final SymbolStatisticsService statisticsService;
    private final RegimeContextService regimeContextService;
    private final WeightApplier weightApplier;
    private final EventPublisher eventPublisher;
    private final OpportunityTransitionEvaluator transitionEvaluator;

    public StrategyOrchestrator(
            OpportunityStateRepository repository,
            TrendDetector trendDetector,
            BreakoutDetector breakoutDetector,
            MomentumDetector momentumDetector,
            VolatilityDetector volatilityDetector,
            SignalFusionEngine fusionEngine,
            ConfidenceEngine confidenceEngine,
            OpportunityLifecycleManager lifecycleManager,
            SymbolStatisticsService statisticsService,
            RegimeContextService regimeContextService,
            WeightApplier weightApplier,
            EventPublisher eventPublisher,
            OpportunityTransitionEvaluator transitionEvaluator
    ) {
        this.repository = repository;
        this.trendDetector = trendDetector;
        this.breakoutDetector = breakoutDetector;
        this.momentumDetector = momentumDetector;
        this.volatilityDetector = volatilityDetector;
        this.fusionEngine = fusionEngine;
        this.confidenceEngine = confidenceEngine;
        this.lifecycleManager = lifecycleManager;
        this.statisticsService = statisticsService;
        this.regimeContextService = regimeContextService;
        this.weightApplier = weightApplier;
        this.eventPublisher = eventPublisher;
        this.transitionEvaluator = transitionEvaluator;
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

        OpportunitySnapshot previous =
                OpportunitySnapshot.from(state);

        SymbolStatisticsState statistics =
                statisticsService.updateAndGet(event);

        RegimeContext context =
                regimeContextService.resolve(
                        event,
                        statistics
                );

        RegimeEvaluation regimeEvaluation = context.regimeEvaluation();

        DetectorSignal trendSignal =
                weightApplier.applyWeight(
                        trendDetector.detect(event),
                        regimeEvaluation.trendWeight()
                );

        DetectorSignal breakoutSignal =
                weightApplier.applyWeight(
                        breakoutDetector.detect(event),
                        regimeEvaluation.breakoutWeight()
                );

        DetectorSignal momentumSignal =
                weightApplier.applyWeight(
                        momentumDetector.detect(event),
                        regimeEvaluation.momentumWeight()
                );

        DetectorSignal volatilitySignal =
                weightApplier.applyWeight(
                        volatilityDetector.detect(event),
                        regimeEvaluation.volatilityWeight()
                );
        List<DetectorSignal> signals =
                List.of(
                        trendSignal,
                        breakoutSignal,
                        momentumSignal,
                        volatilitySignal
                );


        double confidence =
                fusionEngine.calculateConfidence(signals);

        SignalDirection direction =
                fusionEngine.determineDirection(signals);

        confidenceEngine.update(
                state,
                confidence
        );

        state.setDirection(direction);

        state.setMarketRegime(
                regimeEvaluation.regimeState().regime()
        );

        if (direction != SignalDirection.NEUTRAL) {
            state.signalDetected();
        }

        lifecycleManager.evaluate(state);

        OpportunitySnapshot current =
                OpportunitySnapshot.from(state);

        repository.save(state);

        StrategyEvaluationCompletedEvent completedEvent =
                new StrategyEvaluationCompletedEvent(
                        event.symbol(),
                        Instant.now().toEpochMilli(),
                        regimeEvaluation.regimeState().regime(),
                        regimeEvaluation.regimeState().confidence(),
                        state.getConfidence(),
                        state.getDirection(),
                        state.getStatus(),
                        state.getPersistenceCount(),
                        context.statisticalReady(),
                        statistics.getSampleCount()
                );

        log.info(
                """
                ========================================

                STRATEGY EVALUATION COMPLETED

                ========================================

                symbol={}
                statisticalReady={}
                sampleCount={}

                regime={}
                regimeConfidence={}

                confidence={}
                direction={}
                status={}
                persistence={}

                ========================================
                """,

                completedEvent.symbol(),
                completedEvent.statisticalReady(),
                completedEvent.sampleCount(),

                completedEvent.marketRegime(),
                completedEvent.regimeConfidence(),

                completedEvent.confidence(),
                completedEvent.direction(),
                completedEvent.status(),
                completedEvent.persistence()
        );

        eventPublisher.publish(
                completedEvent.symbol(),
                EventType.STRATEGY_EVALUATION_COMPLETED_EVENT,
                completedEvent
        );

        TransitionResult transition =
                transitionEvaluator.evaluate(
                        previous,
                        current
                );

        if (transition.detected()) {
            OpportunityTransitionEvent transitionEvent =
                    new OpportunityTransitionEvent(
                            state.getSymbol(),
                            Instant.now().toEpochMilli(),

                            previous.status(),
                            current.status(),

                            previous.confidence(),
                            current.confidence(),

                            previous.direction(),
                            current.direction(),

                            previous.marketRegime(),
                            current.marketRegime(),

                            transition.reasons()
                    );

            String transitionSummary =
                    String.format(
                            "%s | %s -> %s | %.2f -> %.2f",
                            transitionEvent.symbol(),
                            transitionEvent.previousStatus(),
                            transitionEvent.currentStatus(),
                            transitionEvent.previousConfidence(),
                            transitionEvent.currentConfidence()
                    );

            log.info(
                    """
                    ========================================
            
                    OPPORTUNITY TRANSITION DETECTED
            
                    ========================================
            
                    symbol={}
                    transitionSummary={}
                    transitionReasons={}
            
                    status:
                        {} -> {}
            
                    confidence:
                        {} -> {}
            
                    direction:
                        {} -> {}
            
                    regime:
                        {} -> {}
            
                    ========================================
                    """,

                    transitionEvent.symbol(),
                    transitionSummary,
                    transitionEvent.reasons(),

                    transitionEvent.previousStatus(),
                    transitionEvent.currentStatus(),

                    transitionEvent.previousConfidence(),
                    transitionEvent.currentConfidence(),

                    transitionEvent.previousDirection(),
                    transitionEvent.currentDirection(),

                    transitionEvent.previousRegime(),
                    transitionEvent.currentRegime()
            );

            eventPublisher.publish(
                    transitionEvent.symbol(),
                    EventType.OPPORTUNITY_TRANSITION_EVENT,
                    transitionEvent
            );
        }
    }
}
