package com.harsha.strategy_service.domain.repository;

import com.harsha.strategy_service.domain.model.OpportunityState;

import java.util.Optional;

public interface OpportunityStateRepository {

    Optional<OpportunityState> findBySymbol(
            String symbol
    );

    OpportunityState save(
            OpportunityState state
    );
}
