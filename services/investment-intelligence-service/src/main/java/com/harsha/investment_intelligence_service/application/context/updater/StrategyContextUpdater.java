package com.harsha.investment_intelligence_service.application.context.updater;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.application.context.ContextLimits;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Component;

@Component
public class StrategyContextUpdater {
    private final SymbolContextRepository repository;

    public StrategyContextUpdater(
            SymbolContextRepository repository
    ) {
        this.repository = repository;
    }

    public void update(StrategyEvaluationCompletedEvent event) {
        SymbolContext context = repository.getOrCreate(
                event.symbol()
        );

        context.strategyHistory().addLast(event);

        while (context.strategyHistory().size()
                        > ContextLimits.MAX_STRATEGY_HISTORY
        ) {
            context.strategyHistory().removeFirst();
        }

        repository.save(context);
    }
}
