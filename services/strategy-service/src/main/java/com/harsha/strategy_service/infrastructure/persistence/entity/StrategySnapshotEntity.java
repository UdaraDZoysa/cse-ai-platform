package com.harsha.strategy_service.infrastructure.persistence.entity;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "strategy_snapshot",
        indexes = {
                @Index(
                        name = "idx_strategy_snapshot_symbol_created",
                        columnList = "symbol, createdAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategySnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private double confidence;

    @Enumerated(EnumType.STRING)
    private SignalDirection direction;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus status;

    @Enumerated(EnumType.STRING)
    private MarketRegime marketRegime;

    private int persistenceCount;

    private double trendStrength;

    private double momentumStrength;

    private double volatilityStrength;

    private Instant createdAt;
}
