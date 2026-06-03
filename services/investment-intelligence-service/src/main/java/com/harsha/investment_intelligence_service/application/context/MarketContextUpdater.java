package com.harsha.investment_intelligence_service.application.context;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Component;

@Component
public class MarketContextUpdater {
    private final SymbolContextRepository repository;

    public MarketContextUpdater(SymbolContextRepository repository) {
        this.repository = repository;
    }

    public void update(StockFeatureEvent event) {
        SymbolContext context = repository.getOrCreate(
                event.symbol()
        );

        context.updateFeature(event);

        repository.save(context);
    }
}
