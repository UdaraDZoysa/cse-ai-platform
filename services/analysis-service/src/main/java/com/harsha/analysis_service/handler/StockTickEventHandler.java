package com.harsha.analysis_service.handler;

import com.harsha.analysis_service.service.AnalysisService;
import com.harsha.analysis_service.service.idempotency.IdempotencyService;
import com.harsha.events.market.StockTickEvent;
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
    public String eventType() {
        return "STOCK_TICK_EVENT";
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

        analysisService.analyseEvent(event);

        idempotencyService.markProcessed(eventId);
    }
}
