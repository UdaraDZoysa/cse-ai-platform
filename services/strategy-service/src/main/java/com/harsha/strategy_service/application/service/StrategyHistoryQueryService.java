package com.harsha.strategy_service.application.service;

import com.harsha.strategy_service.domain.model.StrategyHistoryContext;
import com.harsha.strategy_service.infrastructure.persistence.entity.StrategySnapshotEntity;
import com.harsha.strategy_service.infrastructure.persistence.repository.JpaStrategySnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyHistoryQueryService {

    private final JpaStrategySnapshotRepository repository;

    public StrategyHistoryQueryService(
            JpaStrategySnapshotRepository repository
    ) {
        this.repository = repository;
    }

    public StrategyHistoryContext getHistory(
            String symbol
    ) {

        List<StrategySnapshotEntity> snapshots =
                repository.findTop50BySymbolOrderByCreatedAtDesc(
                        symbol
                );

        return new StrategyHistoryContext(

                snapshots.stream()
                        .map(
                                StrategySnapshotEntity::getConfidence
                        )
                        .toList(),

                snapshots.stream()
                        .map(
                                StrategySnapshotEntity::getStatus
                        )
                        .toList(),

                snapshots.stream()
                        .map(
                                StrategySnapshotEntity::getMarketRegime
                        )
                        .toList()
        );
    }
}
