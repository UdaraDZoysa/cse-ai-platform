package com.harsha.analysis_service.application.service.feature.store;

import com.harsha.events.market.StockTickEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RollingWindowStore {
    private final int windowSize;

    private final Map<String, Deque<StockTickEvent>> windows =
            new ConcurrentHashMap<>();

    public RollingWindowStore(
            @Value("${feature.window.size:50}")
            int windowSize
    ) {
        this.windowSize = windowSize;
    }

    public Deque<StockTickEvent> addTick(
            StockTickEvent tick
    ) {
        Deque<StockTickEvent> window =
                windows.computeIfAbsent(
                        tick.symbol(),
                        ignored -> new ArrayDeque<>()
                );

        synchronized (window) {
            if (window.size() >= windowSize) {
                window.removeFirst();
            }

            window.addLast(tick);

            return new ArrayDeque<>(window);
        }
    }

    public Deque<StockTickEvent> currentWindow(
            String symbol
    ) {
        Deque<StockTickEvent> window = windows.get(symbol);

        if (window == null) {
            return new ArrayDeque<>();
        }

        return new ArrayDeque<>(window);
    }
}
