package com.harsha.investment_intelligence_service.application.context.updater;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Component;

@Component
public class MarketContextUpdater {
    private final SymbolContextRepository repository;

    public MarketContextUpdater(SymbolContextRepository repository) {
        this.repository = repository;
    }

    public void update(MarketSnapshotEvent event) {
        SymbolContext context = repository.getOrCreate(
                event.symbol()
        );

        context.updateCurrentMarketSnapshot(event);

        repository.save(context);
    }
}
