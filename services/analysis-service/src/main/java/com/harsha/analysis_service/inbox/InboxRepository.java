package com.harsha.analysis_service.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface InboxRepository extends JpaRepository<InboxEvent, String> {
    @Query(value = """
         SELECT *
            FROM inbox_events
            WHERE status =  'PENDING'
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
    AND e.processingStartedAt < :cutoff
""")
    List<InboxEvent> findStuckProcessingEvents(Instant cutoff);
}
