package com.harsha.analysis_service.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(
                        name = "idx_outbox_status_created_at",
                        columnList = "status, createdAt"
                )
        }
)
@Getter
public class OutboxEvent {
    @Id
    private UUID id;

    private String aggregateId;

    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    private Instant createdAt;

    private int retryCount;

    private Instant lastAttemptAt;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Instant processingStartedAt;

    private Instant processingFinishedAt;

    protected OutboxEvent() {}

    public OutboxEvent(
            UUID id,
            String aggregateId,
            String eventType,
            String payload
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.retryCount = 0;
        this.status = OutboxStatus.PENDING;
    }

    public void markProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.processingFinishedAt = Instant.now();
    }

    public void markFailed() {
        this.status = OutboxStatus.FAILED;
        this.processingFinishedAt = Instant.now();
    }

    public void markPending() {
        this.status = OutboxStatus.PENDING;
    }

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
        this.processingStartedAt = Instant.now();
    }

    public void markAttempt() {
        this.retryCount++;
        this.lastAttemptAt = Instant.now();
    }

    public boolean shouldRetry() {
        return retryCount < 20 &&
                createdAt.plusSeconds(3600).isAfter(Instant.now());
    }

}
