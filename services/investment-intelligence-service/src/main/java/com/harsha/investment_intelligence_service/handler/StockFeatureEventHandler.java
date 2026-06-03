package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class StockFeatureEventHandler implements EventHandler<StockFeatureEvent>{
    private final IdempotencyService idempotencyService;

    public StockFeatureEventHandler(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
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
        System.out.println("In StockFeatureEventHandler. EventId: " + eventId);
    }
}
