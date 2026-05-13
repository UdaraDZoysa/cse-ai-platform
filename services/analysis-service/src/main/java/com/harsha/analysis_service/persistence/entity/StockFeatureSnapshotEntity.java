package com.harsha.analysis_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "stock_feature_snapshots",
        indexes = {
                @Index(name = "idx_symbol_occurred_at", columnList = "symbol, occurredAt"),
                @Index(name = "idx_occurred_at", columnList = "occurredAt")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockFeatureSnapshotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ================= IDENTITY =================

    @Column(nullable = false, length = 20)
    private String symbol;

    // ================= WINDOW =================

    @Column(nullable = false)
    private long windowStartTime;

    @Column(nullable = false)
    private long windowEndTime;

    @Column(nullable = false)
    private int windowSize;

    @Column(nullable = false)
    private String latestEventId;

    // ================= CURRENT MARKET STATE =================

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double change;

    @Column(nullable = false)
    private long volume;

    @Column(nullable = false)
    private double high;

    @Column(nullable = false)
    private double low;

    // ================= TREND =================

    @Column(nullable = false)
    private double upwardRatio;

    @Column(nullable = false)
    private double downwardRatio;

    @Column(nullable = false)
    private double trendPersistence;

    @Column(nullable = false)
    private String trendDirection;

    // ================= MOMENTUM =================

    @Column(nullable = false)
    private double cumulativeReturn;

    @Column(nullable = false)
    private double averageReturn;

    @Column(nullable = false)
    private double returnStdDev;

    @Column(nullable = false)
    private double averageDelta;

    @Column(nullable = false)
    private double acceleration;

    @Column(nullable = false)
    private double positiveMoveRatio;

    @Column(nullable = false)
    private double negativeMoveRatio;

    @Column(nullable = false)
    private double momentumPersistence;

    @Column(nullable = false)
    private double largestUpMove;

    @Column(nullable = false)
    private double largestDownMove;

    @Column(nullable = false)
    private double efficiencyRatio;

    // ================= VOLATILITY =================

    @Column(nullable = false)
    private double volatilityStdDev;

    @Column(nullable = false)
    private double volatilityVariance;

    @Column(nullable = false)
    private String volatilityRegime;

    // ================= MOVING AVERAGES =================

    @Column(nullable = false)
    private double sma5;

    @Column(nullable = false)
    private double sma20;

    @Column(nullable = false)
    private double ema5;

    @Column(nullable = false)
    private double ema20;

    // ================= SYSTEM =================

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}
