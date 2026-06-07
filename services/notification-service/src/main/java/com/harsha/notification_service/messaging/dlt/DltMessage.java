package com.harsha.notification_service.messaging.dlt;

import com.harsha.contracts.messaging.EventType;
import com.harsha.notification_service.exception.ProcessingErrorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(
        name = "dlt_messages",
        indexes = {
                @Index(name = "idx_dlt_created_status_next_attempt", columnList = "status, dlt_created_at, next_attempt_at")
        }
)
@Getter
@Setter
public class DltMessage {
    @Id
    private String id;

    private String aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String targetTopic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

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

    @Column(columnDefinition = "TEXT")
    private String dltFailureReason;

    private Instant updatedAt;

    protected DltMessage() {}

    public DltMessage(
            String id,
            String aggregateId,
            EventType eventType,
            String targetTopic,
            String payload,
            ProcessingErrorType errorType,
            String errorMessage,
            int originRetryCount,
            Instant originCreatedAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.targetTopic = targetTopic;
        this.payload = payload;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.originRetryCount = originRetryCount;
        this.originCreatedAt = originCreatedAt;
        this.status = DltStatus.PENDING;
        this.dltRetryCount = 0;
        this.dltCreatedAt = Instant.now();
        this.nextAttemptAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markAttempt() {
        this.dltRetryCount++;
        this.updatedAt = Instant.now();
    }

    public void markPublished() {
        this.status = DltStatus.PUBLISHED;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = DltStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = DltStatus.FAILED;
        this.updatedAt = Instant.now();
        this.dltFailureReason = reason;
    }

    public void markRetryScheduled() {
        this.status = DltStatus.RETRY_SCHEDULED;
        this.updatedAt = Instant.now();
    }

    public boolean shouldRetry() {
        return dltRetryCount < 20 &&
                dltCreatedAt.plusSeconds(3600).isAfter(Instant.now());
    }
}
