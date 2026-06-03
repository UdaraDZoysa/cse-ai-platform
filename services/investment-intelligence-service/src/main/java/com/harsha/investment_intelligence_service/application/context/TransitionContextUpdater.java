package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Component;

@Component
public class TransitionContextUpdater {
    private final SymbolContextRepository repository;

    public TransitionContextUpdater(
            SymbolContextRepository repository
    ) {
        this.repository = repository;
    }

    public void update(OpportunityTransitionEvent event
    ) {
        SymbolContext context = repository.getOrCreate(
                event.symbol()
        );

        context.transitionHistory().addLast(event);

        while (context.transitionHistory().size()
                        > ContextLimits.MAX_TRANSITION_HISTORY
        ) {
            context.transitionHistory().removeFirst();
        }

        repository.save(context);
    }
}
