package com.harsha.strategy_service.handler;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.strategy_service.application.idempotency.IdempotencyService;
import com.harsha.strategy_service.application.orchestrator.StrategyOrchestrator;
import org.springframework.stereotype.Component;

@Component
public class StockFeatureEventHandler implements EventHandler<StockFeatureEvent>{
    private final IdempotencyService idempotencyService;
    private final StrategyOrchestrator orchestrator;

    public StockFeatureEventHandler(
            IdempotencyService idempotencyService,
            StrategyOrchestrator orchestrator
    ) {
        this.idempotencyService = idempotencyService;
        this.orchestrator = orchestrator;
    }

    @Override
    public EventType eventType() {
        return EventType.STOCK_FEATURE_EVENT;
    }

    @Override
    public Class<StockFeatureEvent> eventClass() {
        return StockFeatureEvent.class;
    }

    @Override
    public void handle(String eventId, StockFeatureEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }
        //For now
        orchestrator.process(event);
        idempotencyService.markProcessed(eventId);
    }
}
