package com.harsha.investment_intelligence_service.infrastructure.storage;

import com.harsha.investment_intelligence_service.domain.model.context.SymbolContext;
import com.harsha.investment_intelligence_service.domain.repository.SymbolContextRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySymbolContextRepository
        implements SymbolContextRepository {

    private final ConcurrentHashMap<String, SymbolContext> store = new ConcurrentHashMap<>();

    @Override
    public SymbolContext getOrCreate(String symbol) {
        return store.computeIfAbsent(
                symbol,
                SymbolContext::new
        );
    }

    @Override
    public Optional<SymbolContext> findBySymbol(String symbol) {
        return Optional.ofNullable(
                store.get(symbol)
        );
    }

    @Override
    public void save(SymbolContext context) {
        store.put(context.symbol(), context);
    }

    @Override
    public Collection<SymbolContext> findAll() {
        return store.values();
    }
}
