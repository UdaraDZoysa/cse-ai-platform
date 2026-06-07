package com.harsha.notification_service.messaging.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface InboxRepository extends JpaRepository<InboxEvent, String> {
    @Query(value = """
         SELECT *
            FROM inbox_events
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
    List<InboxEvent> lockNextBatch();

    @Query("""
    SELECT e
    FROM InboxEvent e
    WHERE e.status = 'PROCESSING'
    AND e.updatedAt < :cutoff
""")
    List<InboxEvent> findStuckProcessingEvents(Instant cutoff);

    @Query("""
            SELECT COUNT(e)
                FROM InboxEvent e
                    WHERE e.status = 'PENDING'
                        OR(
                            e.status = 'RETRY_SCHEDULED'
                                AND e.nextAttemptAt <= :now
                            )
    """)
    long existsByPendingEvents(Instant now);
}
