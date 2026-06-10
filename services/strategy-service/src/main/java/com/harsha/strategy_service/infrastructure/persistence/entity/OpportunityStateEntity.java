package com.harsha.strategy_service.infrastructure.persistence.entity;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "opportunity_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityStateEntity {
    @Id
    private String symbol;

    private double confidence;

    @Enumerated(EnumType.STRING)
    private SignalDirection direction;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus status;

    @Enumerated(EnumType.STRING)
    private MarketRegime marketRegime;

    private double latestTrendStrength;

    private double latestMomentumStrength;

    private double latestVolatilityStrength;

    private Instant firstDetectedAt;

    private Instant lastUpdatedAt;

    private Instant lastSignalAt;

    private int persistenceCount;
}
