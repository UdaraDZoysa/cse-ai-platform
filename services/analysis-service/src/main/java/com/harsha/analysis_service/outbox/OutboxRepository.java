package com.harsha.analysis_service.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query(value = """
         SELECT *
            FROM outbox_events
            WHERE status =  'PENDING'
            ORDER BY created_at ASC 
            LIMIT 20
            FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    List<OutboxEvent> lockNextBatch();

    @Query("""
    SELECT e
    FROM OutboxEvent e
    WHERE e.status = 'PROCESSING'
    AND e.processingStartedAt < :cutoff
""")
    List<OutboxEvent> findStuckProcessingEvents(Instant cutoff);
}
