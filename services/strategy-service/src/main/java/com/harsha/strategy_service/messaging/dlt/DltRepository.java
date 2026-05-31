package com.harsha.strategy_service.messaging.dlt;

import com.harsha.strategy_service.messaging.dlt.DltMessage;
import com.harsha.strategy_service.messaging.inbox.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface DltRepository extends JpaRepository<DltMessage, String> {
    @Query(value = """
         SELECT *
            FROM dlt_messages
            WHERE status =  'PENDING' OR (
                        status = 'RETRY_SCHEDULED'
                        AND next_attempt_at <= NOW()
                        )
            ORDER BY dlt_created_at ASC 
            LIMIT 100
            FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    List<DltMessage> lockNextBatch();

    @Query("""
    SELECT e
    FROM InboxEvent e
    WHERE e.status = 'PROCESSING'
    AND e.updatedAt < :cutoff
""")
    List<DltMessage> findStuckProcessingEvents(Instant cutoff);
}
