package com.harsha.investment_intelligence_service.infrastructure.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "market_snapshot_history"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketSnapshotHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private String company;

    private long occurredAt;

    private double price;

    private double percentageChange;

    private double previousClose;

    private double open;

    private double high;

    private double low;

    private long shareVolume;

    private long tradeVolume;

    private double turnover;

    private double marketCap;

    private long lastTradedTime;
}
