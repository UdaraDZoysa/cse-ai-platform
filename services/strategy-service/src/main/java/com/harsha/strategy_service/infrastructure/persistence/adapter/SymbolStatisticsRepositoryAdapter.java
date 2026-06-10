package com.harsha.strategy_service.infrastructure.persistence.adapter;

import com.harsha.strategy_service.domain.model.SymbolStatisticsState;
import com.harsha.strategy_service.domain.repository.SymbolStatisticsRepository;
import com.harsha.strategy_service.infrastructure.persistence.entity.SymbolStatisticsEntity;
import com.harsha.strategy_service.infrastructure.persistence.mapper.SymbolStatisticsMapper;
import com.harsha.strategy_service.infrastructure.persistence.repository.JpaSymbolStatisticsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SymbolStatisticsRepositoryAdapter
        implements SymbolStatisticsRepository {

    private final JpaSymbolStatisticsRepository repository;
    private final SymbolStatisticsMapper mapper;

    public SymbolStatisticsRepositoryAdapter(
            JpaSymbolStatisticsRepository repository,
            SymbolStatisticsMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SymbolStatisticsState> findBySymbol(
            String symbol
    ) {

        return repository.findById(symbol)
                .map(mapper::toDomain);
    }

    @Override
    public SymbolStatisticsState save(
            SymbolStatisticsState state
    ) {
        SymbolStatisticsEntity saved =
                repository.save(
                        mapper.toEntity(state)
                );

        return mapper.toDomain(saved);
    }
}
