package com.harsha.strategy_service.handler;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.strategy_service.application.service.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class StockFeatureEventHandler implements EventHandler<StockFeatureEvent>{
    private final IdempotencyService idempotencyService;

    public StockFeatureEventHandler(
            IdempotencyService idempotencyService
    ) {
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
        System.out.println(
                "Handle StockFeatureEvent with inbox event id " + eventId +
                        " and StockFeatureEvent symbol " + event.symbol()
        );
    }
}
