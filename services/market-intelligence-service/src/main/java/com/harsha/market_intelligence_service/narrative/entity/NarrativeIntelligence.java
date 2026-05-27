package com.harsha.market_intelligence_service.narrative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false)
    private String symbol;

    private String company;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private Instant generatedAt;

    private Instant expiresAt;

    @OneToMany(
            mappedBy = "intelligence",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<NarrativeSource> sources = new ArrayList<>();
}
