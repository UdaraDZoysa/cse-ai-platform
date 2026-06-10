package com.harsha.investment_intelligence_service.infrastructure.storage.repository;

import com.harsha.investment_intelligence_service.infrastructure.storage.entity.StrategyEvaluationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaStrategyEvaluationHistoryRepository
        extends JpaRepository<StrategyEvaluationHistoryEntity, UUID> {

    @Query(
            value = """
            SELECT *
            FROM strategy_evaluation_history
            WHERE symbol = :symbol
            ORDER BY occurred_at DESC
            LIMIT :limit
            """,
            nativeQuery = true
    )
    List<StrategyEvaluationHistoryEntity> findLatestStrategyEvaluations(
            String symbol,
            int limit
    );

    @Query("""
           select distinct s.symbol
           from StrategyEvaluationHistoryEntity s
           """)
    List<String> findTrackedSymbols();
}
