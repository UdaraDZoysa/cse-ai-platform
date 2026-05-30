package com.harsha.market_intelligence_service.domain.insight.entity;

import com.harsha.contracts.events.market_intelligence.NarrativeSentiment;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "market_insight",
        indexes = {
                @Index(name = "idx_market_insight_symbol", columnList = "symbol")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    private String company;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Enumerated(EnumType.STRING)
    private NarrativeSentiment sentiment;

    private double importanceScore;

    private double persistenceScore;

    private double confidenceScore;

    private Instant generatedAt;

    private Instant expiresAt;

    private String generatedBy;
}
