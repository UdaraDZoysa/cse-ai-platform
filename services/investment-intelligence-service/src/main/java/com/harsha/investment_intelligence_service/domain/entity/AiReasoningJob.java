package com.harsha.investment_intelligence_service.domain.entity;

import com.harsha.investment_intelligence_service.domain.model.reasoning.AIReasoningJobStatus;
import com.harsha.investment_intelligence_service.domain.model.reasoning.ReviewType;
import com.harsha.investment_intelligence_service.exception.ProcessingErrorType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "ai_reasoning_job",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ai_reasoning_job_context_hash",
                        columnNames = "context_hash"
                )
        },
        indexes = {
                @Index(name = "idx_created_at_status_next_attempt_ai_res_job",
                        columnList = "created_at, status, next_attempt_at")
        }

)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiReasoningJob {
    @Id
    private String id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    private AIReasoningJobStatus status;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reasoningContext;

    @Column(name = "context_hash", nullable = false, unique = true, length = 64)
    private String contextHash;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String response;

    @Enumerated(EnumType.STRING)
    private ProcessingErrorType errorType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Instant nextAttemptAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    public void markProcessed() {
        this.status = AIReasoningJobStatus.PROCESSED;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AIReasoningJobStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = AIReasoningJobStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markRetryScheduled() {
        this.status = AIReasoningJobStatus.RETRY_SCHEDULED;
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
        this.updatedAt = Instant.now();
        this.status = AIReasoningJobStatus.PENDING;
        this.retryCount = 0;
        this.nextAttemptAt = Instant.now();
    }
}
