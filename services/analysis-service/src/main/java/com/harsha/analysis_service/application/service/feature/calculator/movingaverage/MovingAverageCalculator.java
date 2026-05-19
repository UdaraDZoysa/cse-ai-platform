package com.harsha.analysis_service.application.service.feature.calculator.movingaverage;

import com.harsha.contracts.events.analysis.MovingAverageFeatures;
import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class MovingAverageCalculator {
    public MovingAverageFeatures calculate(
            Deque<StockTickEvent> window
    ) {
        List<Double> prices = new ArrayList<>();

        for (StockTickEvent tick : window) {
            prices.add(tick.price());
        }

        return new MovingAverageFeatures(
                sma(prices, 5),
                sma(prices, 20),
                ema(prices, 5),
                ema(prices, 20)
        );
    }
    private double sma(
            List<Double> prices,
            int period
    ) {
        if (prices.size() < period) {
            return 0;
        }

        double sum = 0;
        for (int i = (prices.size() - period); i < prices.size(); i++) {
            sum += prices.get(i);
        }

        return sum / period;
    }

    private double ema(
            List<Double> prices,
            int period
    ) {
        if (prices.size() < period) {
            return 0;
        }

        double multiplier = 2.0 / (period + 1);

        double ema = prices.get(0);

        for (double price : prices) {
            ema = ((price - ema) * multiplier) + ema;
        }

        return ema;
    }
}
