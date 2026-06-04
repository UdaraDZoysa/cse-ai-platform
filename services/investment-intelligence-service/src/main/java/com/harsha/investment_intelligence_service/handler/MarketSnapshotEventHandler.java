package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class MarketSnapshotEventHandler implements EventHandler<MarketSnapshotEvent>{
    private final IdempotencyService idempotencyService;

    public MarketSnapshotEventHandler(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EventType eventType() {
        return EventType.MARKET_SNAPSHOT_EVENT;
    }

    @Override
    public Class<MarketSnapshotEvent> eventClass() {
        return MarketSnapshotEvent.class;
    }

    @Override
    public void handle(String eventId, MarketSnapshotEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }
        //For now
        System.out.println("In MarketSnapshotEvent Handler. EventId: " + eventId);
    }
}
