package com.harsha.strategy_service.infrastructure.storage;

import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.domain.repository.OpportunityStateRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOpportunityStateRepository
        implements OpportunityStateRepository {

    private final Map<String, OpportunityState> store = new ConcurrentHashMap<>();

    @Override
    public Optional<OpportunityState> findBySymbol(String symbol) {
        return Optional.ofNullable(
                store.get(symbol)
        );
    }

    @Override
    public void save(OpportunityState opportunityState) {
        store.put(
                opportunityState.getSymbol(),
                opportunityState
        );
    }
}
