package com.harsha.investment_intelligence_service.infrastructure.storage.adapter;

import com.harsha.contracts.events.strategy.OpportunityTransitionEvent;
import com.harsha.investment_intelligence_service.domain.repository.OpportunityTransitionHistoryRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.mapper.OpportunityTransitionHistoryMapper;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaOpportunityTransitionHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpportunityTransitionHistoryRepositoryAdapter
        implements OpportunityTransitionHistoryRepository {

    private final JpaOpportunityTransitionHistoryRepository repository;
    private final OpportunityTransitionHistoryMapper mapper;

    public OpportunityTransitionHistoryRepositoryAdapter(
            JpaOpportunityTransitionHistoryRepository repository,
            OpportunityTransitionHistoryMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(OpportunityTransitionEvent event) {
        repository.save(
                mapper.toEntity(event)
        );
    }

    @Override
    public List<OpportunityTransitionEvent> findLatest(String symbol, int limit) {
        return repository
                .findLatestTransitions(
                        symbol,
                        limit
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
