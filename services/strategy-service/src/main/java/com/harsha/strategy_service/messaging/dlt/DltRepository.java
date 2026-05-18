package com.harsha.strategy_service.messaging.dlt;

import com.harsha.strategy_service.messaging.dlt.DltMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DltRepository extends JpaRepository<DltMessage, String> {
    @Query(value = """
         SELECT *
            FROM dlt_messages
            WHERE status =  'PENDING'
            ORDER BY created_at ASC 
            LIMIT 100
            FOR UPDATE SKIP LOCKED
        """,
            nativeQuery = true
    )
    List<DltMessage> lockNextBatch();
}
