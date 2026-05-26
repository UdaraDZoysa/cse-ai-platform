package com.harsha.market_intelligence_service.orchestration.service;

import com.harsha.market_intelligence_service.orchestration.model.WatchlistSnapshot;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class WatchlistDiffService {
    public WatchlistSnapshot calculate(
            Set<String> previous,
            Set<String> current
    ) {
        Set<String> added = new HashSet<>(current);

        added.removeAll(previous);

        Set<String> removed = new HashSet<>(previous);

        removed.removeAll(current);

        return new WatchlistSnapshot(
                previous,
                current,
                added,
                removed
        );
    }
}
