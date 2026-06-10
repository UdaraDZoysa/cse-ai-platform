package com.harsha.strategy_service.infrastructure.persistence.adapter;

import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.domain.repository.OpportunityStateRepository;
import com.harsha.strategy_service.infrastructure.persistence.entity.OpportunityStateEntity;
import com.harsha.strategy_service.infrastructure.persistence.mapper.OpportunityStateMapper;
import com.harsha.strategy_service.infrastructure.persistence.repository.JpaOpportunityStateRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OpportunityStateRepositoryAdapter
        implements OpportunityStateRepository {

    private final JpaOpportunityStateRepository repository;
    private final OpportunityStateMapper mapper;

    public OpportunityStateRepositoryAdapter(
            JpaOpportunityStateRepository repository,
            OpportunityStateMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<OpportunityState> findBySymbol(
            String symbol
    ) {

        return repository.findById(symbol)
                .map(mapper::toDomain);
    }

    @Override
    public OpportunityState save(
            OpportunityState state
    ) {
        OpportunityStateEntity saved =
                repository.save(
                        mapper.toEntity(state)
                );

        return mapper.toDomain(saved);
    }
}
