package com.harsha.strategy_service.messaging.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.harsha.contracts.messaging.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_pending_poll", columnList = "status, nextAttemptAt, createdAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    private UUID id;

    private String aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode payload;

    private Instant createdAt;

    private int retryCount;

    private Instant nextAttemptAt;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Instant updatedAt;

    public void markProcessed() {
        this.status = OutboxStatus.PROCESSED;
        this.updatedAt = Instant.now();
    }

    public void markDltQueued() {
        this.status = OutboxStatus.DLT_QUEUED;
        this.updatedAt = Instant.now();
    }

    public void markRetryScheduled() {
        this.status = OutboxStatus.RETRY_SCHEDULED;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
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

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.retryCount = 0;
        this.status = OutboxStatus.PENDING;
        this.nextAttemptAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
