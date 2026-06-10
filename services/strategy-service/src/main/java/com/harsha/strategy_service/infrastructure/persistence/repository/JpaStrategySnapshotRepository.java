package com.harsha.strategy_service.infrastructure.persistence.repository;

import com.harsha.strategy_service.infrastructure.persistence.entity.StrategySnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaStrategySnapshotRepository
        extends JpaRepository<StrategySnapshotEntity, UUID> {

    List<StrategySnapshotEntity> findTop50BySymbolOrderByCreatedAtDesc(
            String symbol
    );
}
