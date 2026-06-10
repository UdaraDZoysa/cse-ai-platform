package com.harsha.investment_intelligence_service.infrastructure.storage.entity;

import com.harsha.contracts.events.market_intelligence.NarrativeSentiment;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "market_insight_history"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketInsightHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private long occurredAt;

    private String company;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Enumerated(EnumType.STRING)
    private NarrativeSentiment sentiment;

    private double importanceScore;

    private double confidenceScore;

    private double persistenceScore;

    private long expiresAt;

    private String generatedBy;
}
