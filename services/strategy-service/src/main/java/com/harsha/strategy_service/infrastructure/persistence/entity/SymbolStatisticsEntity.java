package com.harsha.strategy_service.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "symbol_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SymbolStatisticsEntity {
    @Id
    private String symbol;

    private int sampleCount;

    private double meanReturn;

    private double returnVariance;

    private double meanVolatility;

    private double volatilityVariance;

    private double meanMomentumStrength;

    private double momentumVariance;
}
