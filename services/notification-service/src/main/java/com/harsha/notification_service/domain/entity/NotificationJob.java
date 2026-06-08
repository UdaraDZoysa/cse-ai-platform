package com.harsha.notification_service.domain.entity;

import com.harsha.notification_service.domain.model.NotificationChannel;
import com.harsha.notification_service.domain.model.NotificationMessage;
import com.harsha.notification_service.domain.model.NotificationPriority;
import com.harsha.notification_service.domain.model.ProcessingStatus;
import com.harsha.notification_service.exception.ProcessingErrorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(
        name = "notification_job",
        indexes = {
                @Index(
                        name = "idx_notification_job_created_aat_status_next_attempt",
                        columnList = "created_at,status,next_attempt_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationJob {
    @Id
    private String id;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private NotificationMessage message;

    private int retryCount;

    private Instant nextAttemptAt;

    @Enumerated(EnumType.STRING)
    private ProcessingErrorType errorType;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.status = ProcessingStatus.PENDING;
        this.nextAttemptAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = ProcessingStatus.PROCESSING;
    }

    public void markProcessed() {
        this.status = ProcessingStatus.PROCESSED;
    }

    public void markRetryScheduled() {
        this.status = ProcessingStatus.RETRY_SCHEDULED;
    }

    public void markFailed() {
        this.status = ProcessingStatus.FAILED;
    }

    public void markAttempt() {
        this.retryCount++;
    }

    public boolean shouldRetry() {
        return retryCount < 20 &&
                createdAt.plusSeconds(3600).isAfter(Instant.now());
    }
}
