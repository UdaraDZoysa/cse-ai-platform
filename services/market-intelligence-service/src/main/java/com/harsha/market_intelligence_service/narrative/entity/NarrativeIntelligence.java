package com.harsha.market_intelligence_service.narrative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "narrative_intelligence",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "symbol"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NarrativeIntelligence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String rawSearchResult;

    private Instant generatedAt;

    private Instant expiresAt;
}
