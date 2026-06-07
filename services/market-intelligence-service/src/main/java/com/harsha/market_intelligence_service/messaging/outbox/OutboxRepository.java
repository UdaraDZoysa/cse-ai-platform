package com.harsha.market_intelligence_service.messaging.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query(value = """
         SELECT *
            FROM outbox_events
            WHERE status =  'PENDING' OR (
                        status = 'RETRY_SCHEDULED'
                        AND next_attempt_at <= NOW()
                        )
            ORDER BY created_at ASC 
            LIMIT 20
            FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    List<OutboxEvent> lockNextBatch();

    @Query(
            """
            SELECT e 
            FROM OutboxEvent e 
            WHERE e.status = 'PROCESSING'
                AND e.updatedAt < :cutoff
    """)
    List<OutboxEvent> findStuckProcessingJobs(Instant cutoff);

    @Query("""
            SELECT COUNT(e)
                FROM OutboxEvent e
                    WHERE e.status = 'PENDING'
                        OR(
                            e.status = 'RETRY_SCHEDULED'
                                AND e.nextAttemptAt <= :now
                            )
    """)
    long existsByPendingEvents(Instant now);
}
