package com.harsha.investment_intelligence_service.infrastructure.storage.entity;

import com.harsha.contracts.events.strategy.MarketRegime;
import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;
import com.harsha.contracts.events.strategy.TransitionReason;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "opportunity_transition_history",
        indexes = {
                @Index(
                        name = "idx_transition_symbol_time",
                        columnList = "symbol, occurredAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpportunityTransitionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private long occurredAt;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private OpportunityStatus currentStatus;

    private double previousConfidence;

    private double currentConfidence;

    @Enumerated(EnumType.STRING)
    private SignalDirection previousDirection;

    @Enumerated(EnumType.STRING)
    private SignalDirection currentDirection;

    @Enumerated(EnumType.STRING)
    private MarketRegime previousRegime;

    @Enumerated(EnumType.STRING)
    private MarketRegime currentRegime;

    private int persistenceCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "transition_reasons")
    @Enumerated(EnumType.STRING)
    private Set<TransitionReason> reasons;
}
