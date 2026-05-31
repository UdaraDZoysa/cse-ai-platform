package com.harsha.strategy_service.domain.model;

import com.harsha.contracts.events.strategy.OpportunityStatus;
import com.harsha.contracts.events.strategy.SignalDirection;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OpportunityState {
    private final String symbol;
    private double confidence;
    private SignalDirection direction;
    private OpportunityStatus status;
    private double latestTrendStrength;
    private double latestMomentumStrength;
    private double latestVolatilityStrength;
    private Instant firstDetectedAt;
    private Instant lastUpdatedAt;
    private Instant lastSignalAt;
    private int persistenceCount;

    public OpportunityState(String symbol
    ) {
        this.symbol = symbol;
        this.confidence = 0.0;
        this.direction = SignalDirection.NEUTRAL;
        this.status = OpportunityStatus.OPENED;
        this.firstDetectedAt = Instant.now();
        this.lastUpdatedAt = Instant.now();
    }

    public void updateTimestamp() {
        this.lastUpdatedAt = Instant.now();
    }

    public void incrementPersistence() {
        this.persistenceCount++;
    }
}
