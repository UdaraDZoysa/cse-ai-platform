package com.harsha.strategy_service.application.statistics;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import com.harsha.strategy_service.domain.repository.SymbolStatisticsRepository;
import org.springframework.stereotype.Service;

@Service
public class SymbolStatisticsService {
    private final SymbolStatisticsRepository repository;
    private final StatisticsUpdater updater;

    public SymbolStatisticsService(
            SymbolStatisticsRepository repository,
            StatisticsUpdater updater
    ) {
        this.repository = repository;
        this.updater = updater;
    }

    public SymbolStatisticsState updateAndGet(
            StockFeatureEvent event
    ) {
        SymbolStatisticsState state =
                repository.findBySymbol(
                        event.symbol())
                        .orElseGet(() -> new SymbolStatisticsState(
                                        event.symbol()
                                )
                        );

        updater.update(state, event);
        repository.save(state);

        return state;
    }
}
