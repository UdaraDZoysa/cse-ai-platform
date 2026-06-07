package com.harsha.investment_intelligence_service.domain.repository;

import com.harsha.investment_intelligence_service.domain.entity.AiReasoningJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface AiReasoningJobRepository extends JpaRepository<AiReasoningJob, String> {
    @Query(value = """
         SELECT *
            FROM ai_reasoning_job
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
    List<AiReasoningJob> lockNextBatch();

    @Query("""
    SELECT e
    FROM AiReasoningJob e
    WHERE e.status = 'PROCESSING'
    AND e.updatedAt < :cutoff
""")
    List<AiReasoningJob> findStuckProcessingEvents(Instant cutoff);

    @Query("""
            SELECT COUNT(a)
                FROM AiReasoningJob a
                    WHERE a.status = 'PENDING'
                        OR(
                            a.status = 'RETRY_SCHEDULED'
                                AND a.nextAttemptAt <= :now
                            )
    """)
    long existsByPendingJob(Instant now);
}
