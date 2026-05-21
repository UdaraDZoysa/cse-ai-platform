package com.harsha.strategy_service.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class SymbolStatisticsState {
    private final String symbol;
    private long sampleCount;
    private double meanReturn;
    private double returnVariance;
    private double meanVolatility;
    private double volatilityVariance;
    private double meanMomentumStrength;
    private double momentumVariance;

    public SymbolStatisticsState(String symbol) {
        this.symbol = symbol;
    }

    public double getReturnStdDev() {
        return Math.sqrt(returnVariance);
    }

    public double getVolatilityStdDev() {
        return Math.sqrt(volatilityVariance);
    }

    public double getMomentumStdDev() {
        return Math.sqrt(momentumVariance);
    }

    public void incrementSampleCount() {
        sampleCount++;
    }
}
