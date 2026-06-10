package com.harsha.strategy_service.application.service;

import com.harsha.strategy_service.domain.model.OpportunityState;
import com.harsha.strategy_service.infrastructure.persistence.entity.StrategySnapshotEntity;
import com.harsha.strategy_service.infrastructure.persistence.repository.JpaStrategySnapshotRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StrategySnapshotService {

    private final JpaStrategySnapshotRepository repository;

    public StrategySnapshotService(
            JpaStrategySnapshotRepository repository
    ) {
        this.repository = repository;
    }

    public void createSnapshot(
            OpportunityState state
    ) {

        StrategySnapshotEntity snapshot =
                StrategySnapshotEntity.builder()
                        .symbol(state.getSymbol())
                        .confidence(state.getConfidence())
                        .direction(state.getDirection())
                        .status(state.getStatus())
                        .marketRegime(state.getMarketRegime())
                        .persistenceCount(state.getPersistenceCount())
                        .trendStrength(state.getLatestTrendStrength())
                        .momentumStrength(state.getLatestMomentumStrength())
                        .volatilityStrength(state.getLatestVolatilityStrength())
                        .createdAt(Instant.now())
                        .build();

        repository.save(snapshot);
    }
}
