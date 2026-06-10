package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import com.harsha.investment_intelligence_service.domain.repository.OpportunityTransitionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OpportunityTransEventHandler implements EventHandler<OpportunityTransitionEvent> {
    private final IdempotencyService idempotencyService;
    private final OpportunityTransitionHistoryRepository repository;
    private static final Logger log = LoggerFactory.getLogger(OpportunityTransEventHandler.class);

    public OpportunityTransEventHandler(
            IdempotencyService idempotencyService,
            OpportunityTransitionHistoryRepository repository
    ) {
        this.idempotencyService = idempotencyService;
        this.repository = repository;
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

        repository.save(event);

        log.info(
                """
                Opportunity Transition context updated.
                
                Symbol: {}
                Timestamp: {}
                
                """,
                event.symbol(),
                Instant.now()
        );

        idempotencyService.markProcessed(eventId);
    }
}
