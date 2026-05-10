package com.harsha.analysis_service.handler;

import com.harsha.analysis_service.service.idempotency.IdempotencyService;
import com.harsha.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

@Component
public class StockTickEventHandler implements EventHandler<StockTickEvent> {
    private final IdempotencyService idempotencyService;

    public StockTickEventHandler(
            IdempotencyService idempotencyService
    ) {
        this.idempotencyService = idempotencyService;
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
        //For now
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        System.out.println(
                "Processing stock tick: " + event.symbol()
        );

        idempotencyService.markProcessed(eventId);
    }
}
