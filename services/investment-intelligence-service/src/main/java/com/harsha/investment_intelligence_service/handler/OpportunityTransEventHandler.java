package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class OpportunityTransEventHandler implements EventHandler<OpportunityTransitionEvent> {
    private final IdempotencyService idempotencyService;

    public OpportunityTransEventHandler(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EventType eventType() {
        return EventType.OPPORTUNITY_TRANSITION_EVENT;
    }

    @Override
    public Class<OpportunityTransitionEvent> eventClass() {
        return OpportunityTransitionEvent.class;
    }

    @Override
    public void handle(String eventId, OpportunityTransitionEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }
        //For now
        System.out.println("In OpportunityTransEvent Handler");
    }
}
