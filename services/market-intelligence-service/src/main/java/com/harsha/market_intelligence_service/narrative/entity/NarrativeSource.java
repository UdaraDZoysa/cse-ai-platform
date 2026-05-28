package com.harsha.market_intelligence_service.narrative.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "narrative_source",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "source_url"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NarrativeSource {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sourceUrl;

    private Instant publishedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intelligence_id")
    private NarrativeIntelligence intelligence;
}
