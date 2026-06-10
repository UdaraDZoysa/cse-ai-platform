package com.harsha.analysis_service.handler;

import com.harsha.analysis_service.application.service.AnalysisService;
import com.harsha.analysis_service.application.service.idempotency.IdempotencyService;
import com.harsha.analysis_service.domain.repository.StockTickRepository;
import com.harsha.analysis_service.persistence.adapter.StockTickRepositoryAdapter;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.messaging.EventType;
import org.springframework.stereotype.Component;

@Component
public class StockTickEventHandler implements EventHandler<StockTickEvent> {
    private final IdempotencyService idempotencyService;
    private final AnalysisService analysisService;
    private final StockTickRepository repository;

    public StockTickEventHandler(
            IdempotencyService idempotencyService,
            AnalysisService analysisService,
            StockTickRepository repository
    ) {
        this.idempotencyService = idempotencyService;
        this.analysisService = analysisService;
        this.repository = repository;
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

        repository.save(event);

        analysisService.analyseEvent(eventId, event);

        idempotencyService.markProcessed(eventId);
    }
}
