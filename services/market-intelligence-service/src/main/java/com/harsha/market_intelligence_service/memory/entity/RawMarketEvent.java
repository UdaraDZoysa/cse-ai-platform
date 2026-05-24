package com.harsha.market_intelligence_service.memory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "raw_market_event",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"externalId", "sourceType"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawMarketEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String externalId;

    private String sourceType;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String company;

    private String symbol;

    private String category;

    private Instant publishedAt;

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    private Instant createdAt;
}
