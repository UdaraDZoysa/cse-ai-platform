package com.harsha.strategy_service.domain.repository;

import com.harsha.strategy_service.domain.model.SymbolStatisticsState;

import java.util.Optional;

public interface SymbolStatisticsRepository {
    Optional<SymbolStatisticsState> findBySymbol(String symbol);

    void save(SymbolStatisticsState state);
}
