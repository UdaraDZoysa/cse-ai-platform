package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class MarketInsightGenEventHandler implements EventHandler<MarketInsightGeneratedEvent>{
    private final IdempotencyService idempotencyService;

    public MarketInsightGenEventHandler(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EventType eventType() {
        return EventType.MARKET_INSIGHT_GENERATED_EVENT;
    }

    @Override
    public Class<MarketInsightGeneratedEvent> eventClass() {
        return MarketInsightGeneratedEvent.class;
    }

    @Override
    public void handle(String eventId, MarketInsightGeneratedEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }
        //For now
        System.out.println("In MarketInsightGenEvent Handler");
    }
}
