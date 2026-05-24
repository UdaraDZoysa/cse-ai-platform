package com.harsha.market_intelligence_service.filtering.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TrackedSymbolService {
    //For now
    public Set<String> getTrackedSymbols() {
        return Set.of(
                "JKH.N0000",
                "SAMP.N0000",
                "COMB.N0000",
                "HNB.N0000",
                "LIOC.N0000",
                "DIAL.N0000"
        );
    }
}
