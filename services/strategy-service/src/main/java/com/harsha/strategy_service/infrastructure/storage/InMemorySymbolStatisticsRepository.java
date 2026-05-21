package com.harsha.strategy_service.infrastructure.storage;

import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import com.harsha.strategy_service.domain.repository.SymbolStatisticsRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySymbolStatisticsRepository
        implements SymbolStatisticsRepository {

    private final Map<String, SymbolStatisticsState> storage =
            new ConcurrentHashMap<>();

    @Override
    public Optional<SymbolStatisticsState> findBySymbol(String symbol) {
        return Optional.ofNullable(
                storage.get(symbol)
        );
    }

    @Override
    public void save(SymbolStatisticsState state) {
        storage.put(
                state.getSymbol(),
                state
        );
    }
}
