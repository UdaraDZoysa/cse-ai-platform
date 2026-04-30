package com.harsha.market_data_service.feature;

import com.harsha.events.market.StockTickEvent;
import com.harsha.events.market.StockFeatures;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FeatureExtractor {
    private final Map<String, StockTickEvent> lastState = new ConcurrentHashMap<>();
    private final Map<String, String> lastTrend = new ConcurrentHashMap<>();
    private final Map<String, Integer> trendCounter = new ConcurrentHashMap<>();
    private final Map<String, Integer> oppositeStreak = new ConcurrentHashMap<>();
    private static final int TREND_TOLERANCE = 1;

    public StockFeatures extract(StockTickEvent current) {
        StockTickEvent prev = lastState.get(current.symbol());

        if (prev == null) {
            lastState.put(current.symbol(), current);
            trendCounter.put(current.symbol(), 0);
            return null;
        }

        //Trend
        String trend;
        if (current.price() > prev.price()) {
            trend = "UP";
        } else if (current.price() < prev.price()) {
            trend = "DOWN";
        } else {
            trend = "FLAT";
        }

        //Trend Strength (with tolerance + decay)
        int strength;
        String prevTrend = lastTrend.get(current.symbol());
        int prevStrength = trendCounter.getOrDefault(current.symbol(), 1);
        int oppStreak = oppositeStreak.getOrDefault(current.symbol(), 0);
        if (trend.equals(prevTrend)) {
            strength = prevStrength + 1;
            oppositeStreak.put(current.symbol(), 0);
        } else if (trend.equals("FLAT")) {
            strength = Math.max(1, prevStrength);
        } else {
            // opposite direction
            oppStreak++;

            if (oppStreak <= TREND_TOLERANCE) {
                // tolerate first opposite move (noise)
                strength = prevStrength;
            } else {
                // real reversal confirmed
                strength = 1;
            }
            oppositeStreak.put(current.symbol(), oppStreak);
        }
        trendCounter.put(current.symbol(), strength);
        lastTrend.put(current.symbol(), trend);

        //Price change %
        double changePercent =
                (((double) current.price() - (double) prev.price()) / (double) prev.price()) * 100;

        //Volatility
        double volatility = current.high() - current.low();

        //Volume spike
        boolean volumeSpike = current.volume() > prev.volume() * 1.5;

        //Update state
        lastState.put(current.symbol(), current);

        return new StockFeatures(
                current.symbol(),
                trend,
                strength,
                changePercent,
                volatility,
                volumeSpike,
                current.price(),
                current.high(),
                current.low(),
                current.change()
        );
    }
}
