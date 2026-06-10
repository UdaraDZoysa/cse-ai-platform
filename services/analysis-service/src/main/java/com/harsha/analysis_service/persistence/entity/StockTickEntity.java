package com.harsha.analysis_service.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "stock_tick",
        indexes = {
                @Index(
                        name = "idx_market_tick_symbol_time",
                        columnList = "symbol, occurredAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTickEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String symbol;

    private long occurredAt;

    private double price;

    private double change;

    private long volume;

    private double high;

    private double low;

    private long lastTradedTime;
}
