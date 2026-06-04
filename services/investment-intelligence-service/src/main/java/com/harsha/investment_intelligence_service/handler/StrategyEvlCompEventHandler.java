package com.harsha.investment_intelligence_service.handler;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.contracts.messaging.EventType;
import com.harsha.investment_intelligence_service.application.context.updater.StrategyContextUpdater;
import com.harsha.investment_intelligence_service.application.idempotency.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StrategyEvlCompEventHandler implements EventHandler<StrategyEvaluationCompletedEvent>{
    private final IdempotencyService idempotencyService;
    private final StrategyContextUpdater strategyContextUpdater;
    private static final Logger log = LoggerFactory.getLogger(StrategyEvlCompEventHandler.class);

    public StrategyEvlCompEventHandler(
            IdempotencyService idempotencyService,
            StrategyContextUpdater strategyContextUpdater
    ) {
        this.idempotencyService = idempotencyService;
        this.strategyContextUpdater = strategyContextUpdater;
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

        strategyContextUpdater.update(event);

        log.info(
                """
                Strategy Evaluation Completed context updated.
                
                Symbol: {}
                Timestamp: {}
                
                """,
                event.symbol(),
                Instant.now()
        );
    }
}
