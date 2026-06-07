package com.harsha.notification_service.handler;

import com.harsha.contracts.events.investment_intelligence.InvestmentInsightGeneratedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.notification_service.application.idempotency.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InvestmentInsightGenEventHandler implements EventHandler<InvestmentInsightGeneratedEvent>{
    private final IdempotencyService idempotencyService;
    private static final Logger log = LoggerFactory.getLogger(InvestmentInsightGenEventHandler.class);

    public InvestmentInsightGenEventHandler(
            IdempotencyService idempotencyService
    ) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EventType eventType() {
        return EventType.INVESTMENT_INSIGHT_GENERATED_EVENT;
    }

    @Override
    public Class<InvestmentInsightGeneratedEvent> eventClass() {
        return InvestmentInsightGeneratedEvent.class;
    }

    @Override
    public void handle(String eventId, InvestmentInsightGeneratedEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }

        System.out.println("Inside Investment Insight Generation Event handler");

        idempotencyService.markProcessed(eventId);
    }
}
