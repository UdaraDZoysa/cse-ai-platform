package com.harsha.investment_intelligence_service.infrastructure.storage.entity;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "strategy_evaluation_history",
        indexes = {
                @Index(
                        name = "idx_strategy_eval_symbol_time",
                        columnList = "symbol, occurred_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyEvaluationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private long occurredAt;

    @Enumerated(EnumType.STRING)
    private MarketRegime marketRegime;

    private double regimeConfidence;

    private double confidence;

    @Enumerated(EnumType.STRING)
    private SignalDirection direction;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus status;

    private int persistence;

    private boolean statisticalReady;

    private int sampleCount;
}
