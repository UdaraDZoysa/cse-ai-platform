package com.harsha.market_intelligence_service.domain.insight.entity;

import com.harsha.market_intelligence_service.exception.ProcessingErrorType;
import com.harsha.market_intelligence_service.domain.insight.model.InsightJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "insight_generation_job",
        indexes = {
                @Index(name = "idx_created_at_status_Insight_gen_job", columnList = "created_at, status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightGenerationJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private InsightJobStatus status;

    private int retryCount;

    private Instant createdAt;

    private Instant nextAttemptAt;

    @Enumerated(EnumType.STRING)
    private ProcessingErrorType errorType;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    private Instant failedAt;

    private Instant updatedAt;

    public void markProcessed() {
        this.status = InsightJobStatus.PROCESSED;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        this.status = InsightJobStatus.FAILED;
        this.failedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markProcessing() {
        this.status = InsightJobStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markAttempt() {
        this.retryCount++;
        this.updatedAt = Instant.now();
    }

    public void markSkipped() {
        this.status = InsightJobStatus.SKIPPED;
        this.updatedAt = Instant.now();
    }

    public void markRetryScheduled() {
        this.status = InsightJobStatus.RETRY_SCHEDULED;
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
        this.status = InsightJobStatus.PENDING;
    }
}
