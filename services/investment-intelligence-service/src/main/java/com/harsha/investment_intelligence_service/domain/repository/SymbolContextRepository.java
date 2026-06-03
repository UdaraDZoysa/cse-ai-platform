package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;

import java.util.Collection;
import java.util.Optional;

public interface SymbolContextRepository {
    SymbolContext getOrCreate(String symbol);

    Optional<SymbolContext> findBySymbol(String symbol);

    void save(SymbolContext context);

    Collection<SymbolContext> findAll();
}
