package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.market_intelligence.MarketInsightGeneratedEvent;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Component;

@Component
public class InsightContextUpdater {
    private final SymbolContextRepository repository;
    private final InsightRetentionPolicy retentionPolicy;

    public InsightContextUpdater(
            SymbolContextRepository repository,
            InsightRetentionPolicy retentionPolicy
    ) {
        this.repository = repository;
        this.retentionPolicy = retentionPolicy;
    }

    public void update(MarketInsightGeneratedEvent event) {
        SymbolContext context = repository.getOrCreate(
                event.symbol()
        );

        context.activeInsights()
                .removeIf(
                        insight ->
                                !retentionPolicy.shouldRetain(insight)
                );

        context.activeInsights().add(event);

        repository.save(context);
    }
}
