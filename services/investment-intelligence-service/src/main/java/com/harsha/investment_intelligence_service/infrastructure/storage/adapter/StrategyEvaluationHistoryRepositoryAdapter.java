package com.harsha.investment_intelligence_service.infrastructure.storage.adapter;

import com.harsha.contracts.events.strategy.StrategyEvaluationCompletedEvent;
import com.harsha.investment_intelligence_service.domain.repository.StrategyEvaluationHistoryRepository;
import com.harsha.investment_intelligence_service.infrastructure.storage.mapper.StrategyEvaluationHistoryMapper;
import com.harsha.investment_intelligence_service.infrastructure.storage.repository.JpaStrategyEvaluationHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrategyEvaluationHistoryRepositoryAdapter
        implements StrategyEvaluationHistoryRepository {

    private final JpaStrategyEvaluationHistoryRepository repository;
    private final StrategyEvaluationHistoryMapper mapper;

    public StrategyEvaluationHistoryRepositoryAdapter(
            JpaStrategyEvaluationHistoryRepository repository,
            StrategyEvaluationHistoryMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(
            StrategyEvaluationCompletedEvent event
    ) {
        repository.save(
                mapper.toEntity(event)
        );
    }

    @Override
    public List<StrategyEvaluationCompletedEvent> findLatest(
            String symbol,
            int limit
    ) {

        return repository
                .findLatestStrategyEvaluations(
                        symbol,
                        limit
                )
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<String> findTrackedSymbols() {
        return repository.findTrackedSymbols();
    }
}
