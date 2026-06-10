package com.harsha.analysis_service.persistence.repository;

import com.harsha.analysis_service.persistence.entity.StockTickEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface JpaStockTickRepository extends JpaRepository<StockTickEntity, UUID> {
    @Query(
            value = """
            SELECT *
            FROM stock_tick
            WHERE symbol = :symbol
            ORDER BY occurred_at DESC
            LIMIT :limit
            """,
            nativeQuery = true
    )
    List<StockTickEntity> findLatestTicks(
            String symbol,
            int limit
    );

    @Query("""
            SELECT DISTINCT m.symbol
            FROM StockTickEntity m
            """)
    List<String> findTrackedSymbols();

    @Modifying
    @Query("""
       DELETE
       FROM StockTickEntity s
       WHERE s.occurredAt < :cutoff
       """)
    int deleteOlderThan(
            long cutoff
    );
}
