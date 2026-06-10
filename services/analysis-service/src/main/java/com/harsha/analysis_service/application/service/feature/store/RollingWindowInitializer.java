package com.harsha.analysis_service.application.service.feature.store;

import com.harsha.analysis_service.domain.repository.StockTickRepository;
import com.harsha.contracts.events.market.StockTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RollingWindowInitializer {
    private final StockTickRepository repository;
    private final RollingWindowStore rollingWindowStore;
    private final int windowSize;
    private static final Logger log = LoggerFactory.getLogger(RollingWindowInitializer.class);

    public RollingWindowInitializer(
            StockTickRepository repository,
            RollingWindowStore rollingWindowStore,
            @Value("${feature.window.size:100}")
            int windowSize
    ) {
        this.repository = repository;
        this.rollingWindowStore = rollingWindowStore;
        this.windowSize = windowSize;
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initialize() {
        List<String> symbols =
                repository.findTrackedSymbols();

        for (String symbol : symbols) {
            List<StockTickEvent> ticks = repository.findLatestBySymbol(
                    symbol,
                    windowSize
            );

            Collections.reverse(ticks);

            for (StockTickEvent tick : ticks) {
                rollingWindowStore.restore(
                        tick
                );
            }
            log.info(
                    "Restored {} ticks for {}",
                    ticks.size(),
                    symbol
            );
        }
    }
}
