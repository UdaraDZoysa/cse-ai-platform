package com.harsha.investment_intelligence_service.messaging.inbox;

import com.harsha.contracts.messaging.EventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(
        name = "inbox_events",
        indexes = {
                @Index(name = "idx_inbox_status_created_next_attempt", columnList = "status, created_at, next_attempt_at")
        }
)
@Getter
@Setter
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

    private Instant nextAttemptAt;

    @Enumerated(EnumType.STRING)
    private InboxStatus status;

    private Instant updatedAt;

    protected InboxEvent() {}

    public InboxEvent(String id, String aggregateId, EventType eventType, String payload) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.status = InboxStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void markProcessed() {
        this.status = InboxStatus.PROCESSED;
        this.updatedAt = Instant.now();
    }

    public void markDltQueued() {
        this.status = InboxStatus.DLT_QUEUED;
        this.updatedAt = Instant.now();
    }

    public void markRetryScheduled() {
        this.status = InboxStatus.RETRY_SCHEDULED;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = InboxStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markAttempt() {
        this.retryCount++;
        this.updatedAt = Instant.now();
    }

    public boolean shouldRetry() {
        return retryCount < 20 &&
                createdAt.plusSeconds(3600).isAfter(Instant.now());
    }
}
