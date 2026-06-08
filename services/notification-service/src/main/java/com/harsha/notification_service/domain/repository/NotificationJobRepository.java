package com.harsha.notification_service.domain.repository;

import com.harsha.notification_service.domain.entity.NotificationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, String> {
    @Query(value = """
         SELECT *
            FROM notification_job
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
    List<NotificationJob> lockNextBatch();

    @Query("""
    SELECT n
    FROM NotificationJob n
    WHERE n.status = 'PROCESSING'
    AND n.updatedAt < :cutoff
""")
    List<NotificationJob> findStuckProcessingEvents(Instant cutoff);

    @Query("""
            SELECT COUNT(n)
                FROM NotificationJob n
                    WHERE n.status = 'PENDING'
                        OR(
                            n.status = 'RETRY_SCHEDULED'
                                AND n.nextAttemptAt <= :now
                            )
    """)
    long existsByPendingJob(Instant now);
}
