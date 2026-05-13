package com.harsha.analysis_service.inbox;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

@Entity
@Table(
        name = "inbox_events",
        indexes = {
                @Index(name = "idx_inbox_processed_created", columnList = "processed, created_at")
        }
)
public class InboxEvent {
    @Id
    private String id;

    private String aggregateId;

    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String payload;

    private int retryCount;

    private Instant createdAt;

    private Instant lastAttemptAt;

    @Enumerated(EnumType.STRING)
    private InboxStatus status;

    private Instant processingStartedAt;

    protected InboxEvent() {}

    public InboxEvent(String id, String aggregateId, String eventType, String payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.status = InboxStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public Instant getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void markProcessed() {
        this.status = InboxStatus.PROCESSED;
    }

    public void markFailed() {
        this.status = InboxStatus.FAILED;
    }

    public void markPending() {
        this.status = InboxStatus.PENDING;
    }

    public void markProcessing() {
        this.status = InboxStatus.PROCESSING;
        this.processingStartedAt = Instant.now();
    }

    public void markAttempt() {
        this.retryCount++;
        this.lastAttemptAt = Instant.now();
    }

    public boolean shouldRetry() {
        return retryCount < 50 &&
                createdAt.plusSeconds(3600).isAfter(Instant.now());
    }


}
