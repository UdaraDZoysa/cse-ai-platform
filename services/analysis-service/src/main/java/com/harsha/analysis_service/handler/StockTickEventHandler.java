package com.harsha.analysis_service.handler;

import com.harsha.analysis_service.application.service.AnalysisService;
import com.harsha.analysis_service.application.service.idempotency.IdempotencyService;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.messaging.EventType;
import org.springframework.stereotype.Component;

@Component
public class StockTickEventHandler implements EventHandler<StockTickEvent> {
    private final IdempotencyService idempotencyService;
    private final AnalysisService analysisService;

    public StockTickEventHandler(
            IdempotencyService idempotencyService,
            AnalysisService analysisService
    ) {
        this.idempotencyService = idempotencyService;
        this.analysisService = analysisService;
    }

    @Override
    public EventType eventType() {
        return EventType.STOCK_TICK_EVENT;
    }

    @Override
    public Class<StockTickEvent> eventClass() {
        return StockTickEvent.class;
    }

    @Override
    public void handle(String eventId, StockTickEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        analysisService.analyseEvent(eventId, event);

        idempotencyService.markProcessed(eventId);
    }
}
