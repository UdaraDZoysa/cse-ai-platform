package com.harsha.market_data_service.diff;

import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.market_data_service.model.StockTickState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StockTickDiffEngine {
    private static final double VOLUME_SURGE_THRESHOLD = 1.25;
    private final Map<String, StockTickState> states = new ConcurrentHashMap<>();
    private final Map<String, Double> lastPrices = new ConcurrentHashMap<>();

    public boolean hasChanged(
            StockTickEvent event
    ) {

        StockTickState previous =
                states.get(event.symbol());

        if (previous == null) {
            states.put(
                    event.symbol(),
                    new StockTickState(
                            event.price(),
                            event.volume(),
                            event.high(),
                            event.low()
                    )
            );
            return true;
        }

        boolean priceChanged = Math.abs(previous.price() - event.price()) > 0.0001;

        boolean volumeSurged = event.volume() > previous.volume() * VOLUME_SURGE_THRESHOLD;

        boolean newHigh = event.high() > previous.high();

        boolean newLow = event.low() < previous.low();

        states.put(
                event.symbol(),
                new StockTickState(
                        event.price(),
                        event.volume(),
                        event.high(),
                        event.low()
                )
        );

        return priceChanged
                || volumeSurged
                || newHigh
                || newLow;
    }
}
