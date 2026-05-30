package com.harsha.market_intelligence_service.domain.insight.repository;
import com.harsha.market_intelligence_service.domain.insight.entity.InsightGenerationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface InsightGenJobRepository extends JpaRepository<InsightGenerationJob, Long> {
    @Query(value = """
         SELECT *
            FROM insight_generation_job
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
    List<InsightGenerationJob> lockNextBatch();

    @Query(
            """
            SELECT e 
            FROM InsightGenerationJob e 
            WHERE e.status = 'PROCESSING'
                AND e.updatedAt < :cutoff
    """)
    List<InsightGenerationJob> findStuckProcessingJobs(Instant cutoff);

}
