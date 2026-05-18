package com.harsha.strategy_service.messaging.inbox;

import com.harsha.contracts.messaging.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Getter
@Table(
        name = "inbox_events",
        indexes = {
                @Index(name = "idx_inbox_processed_created", columnList = "status, created_at")
        }
)
public class InboxEvent {
    @Id
    private String id;

    @Column(nullable = false)
    private String aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastAttemptAt;

    @Enumerated(EnumType.STRING)
    private InboxStatus status;

    private Instant processingStartedAt;

    protected InboxEvent() {}

    public InboxEvent(String id, String aggregateId, EventType eventType, String payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.status = InboxStatus.PENDING;
    }

    public void markProcessed() {
        this.status = InboxStatus.PROCESSED;
    }

    public void markDltQueued() {
        this.status = InboxStatus.DLT_QUEUED;
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
        return retryCount < 20 &&
                createdAt.plusSeconds(3600).isAfter(Instant.now());
    }

}
