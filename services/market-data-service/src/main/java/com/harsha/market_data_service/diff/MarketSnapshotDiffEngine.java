package com.harsha.market_data_service.diff;

import com.harsha.contracts.events.market.MarketSnapshotEvent;
import com.harsha.market_data_service.model.MarketSnapshotState;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MarketSnapshotDiffEngine {
    private static final double TURNOVER_SURGE_THRESHOLD = 1.25;

    private static final double MARKET_CAP_CHANGE_THRESHOLD = 0.01;

    private static final double PERCENTAGE_CHANGE_THRESHOLD = 0.50;

    private final Map<String, MarketSnapshotState> states =
            new ConcurrentHashMap<>();

    public boolean hasChanged(
            MarketSnapshotEvent event
    ) {

        MarketSnapshotState previous = states.get(event.symbol());

        if (previous == null) {
            states.put(
                    event.symbol(),
                    createState(event)
            );
            return true;
        }

        boolean priceChanged = Math.abs(event.price() - previous.price()) > 0.0001;

        boolean percentageMoved =
                Math.abs(event.percentageChange() - previous.percentageChange()) >= PERCENTAGE_CHANGE_THRESHOLD;

        boolean turnoverSurged = event.turnover() > previous.turnover() * TURNOVER_SURGE_THRESHOLD;

        boolean marketCapMoved =
                Math.abs(event.marketCap() - previous.marketCap())
                        / previous.marketCap() >= MARKET_CAP_CHANGE_THRESHOLD;

        boolean newHigh = event.high() > previous.high();

        boolean newLow = event.low() < previous.low();

        boolean changed =
                percentageMoved
                        || priceChanged
                        || turnoverSurged
                        || marketCapMoved
                        || newHigh
                        || newLow;

        states.put(
                event.symbol(),
                createState(event)
        );

        return changed;
    }

    private MarketSnapshotState createState(
            MarketSnapshotEvent event
    ) {
        return new MarketSnapshotState(
                event.price(),
                event.percentageChange(),
                event.shareVolume(),
                event.tradeVolume(),
                event.turnover(),
                event.marketCap(),
                event.high(),
                event.low()
        );
    }
}
