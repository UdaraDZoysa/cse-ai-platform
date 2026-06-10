package com.harsha.investment_intelligence_service.infrastructure.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "watchlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistEntity {
    @Id
    private String watchlistId;

    private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "watchlist_symbols",
            joinColumns = @JoinColumn(
                    name = "watchlist_id"
            )
    )
    @Column(name = "symbol")
    private Set<String> symbols;
}
