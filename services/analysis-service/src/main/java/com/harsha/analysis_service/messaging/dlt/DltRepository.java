package com.harsha.analysis_service.messaging.dlt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DltRepository extends JpaRepository<DltMessage, String> {
    @Query(value = """
         SELECT *
            FROM dlt_messages
            WHERE status =  'PENDING'
            ORDER BY dlt_created_at ASC 
            LIMIT 100
            FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    List<DltMessage> lockNextBatch();
}
