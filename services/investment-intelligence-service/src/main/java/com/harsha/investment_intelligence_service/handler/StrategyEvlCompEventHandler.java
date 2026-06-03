package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.springframework.stereotype.Component;

@Component
public class StrategyEvlCompEventHandler implements EventHandler<StrategyEvaluationCompletedEvent>{
    private final IdempotencyService idempotencyService;

    public StrategyEvlCompEventHandler(IdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @Override
    public EventType eventType() {
        return EventType.STRATEGY_EVALUATION_COMPLETED_EVENT;
    }

    @Override
    public Class<StrategyEvaluationCompletedEvent> eventClass() {
        return StrategyEvaluationCompletedEvent.class;
    }

    @Override
    public void handle(String eventId, StrategyEvaluationCompletedEvent event) {
        if (idempotencyService.alreadyProcessed(eventId)) {
            return;
        }
        //For now
        System.out.println("In StrategyEvalComp Handler");
    }
}
