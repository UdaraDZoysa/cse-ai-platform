package com.harsha.market_intelligence_service.messaging.dlt;

import com.fasterxml.jackson.databind.JsonNode;
import com.harsha.contracts.messaging.EventType;
import com.harsha.market_intelligence_service.exception.ProcessingErrorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(
        name = "dlt_messages",
        indexes = {
                @Index(name = "idx_dlt_message_created", columnList = "status, dlt_created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DltMessage {
    @Id
    private String id;

    private String aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String targetTopic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    @Enumerated(EnumType.STRING)
    private ProcessingErrorType errorType;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    private DltStatus status;

    //Retry count for happy path
    private int originRetryCount;

    //Original message created time
    private Instant originCreatedAt;

    private int dltRetryCount;

    private Instant dltCreatedAt;

    private Instant nextAttemptAt;

    private String dltFailureReason;

    private Instant updatedAt;

    public void markAttempt() {
        this.dltRetryCount++;
        this.updatedAt = Instant.now();
    }

    public void markPublished() {
        this.status = DltStatus.PUBLISHED;
        this.updatedAt = Instant.now();
    }

    public void markPending() {
        this.status = DltStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = DltStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markRetryScheduled() {
        this.status = DltStatus.RETRY_SCHEDULED;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = DltStatus.FAILED;
        this.updatedAt = Instant.now();
        this.dltFailureReason = reason;
    }

    public boolean shouldRetry() {
        return dltRetryCount < 20 &&
                dltCreatedAt.plusSeconds(3600).isAfter(Instant.now());
    }

    @PrePersist
    public void prePersist() {
        this.dltCreatedAt = Instant.now();
        this.dltRetryCount = 0;

    }
}
