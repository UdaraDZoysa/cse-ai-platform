package com.harsha.analysis_service.application.service.feature.calculator.volatility;

import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.events.analysis.VolatilityFeatures;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class VolatilityCalculator {
    public VolatilityFeatures calculate(
            Deque<StockTickEvent> window
    ) {
        if (window.size() < 2) {
            return new VolatilityFeatures(0, 0, "LOW");
        }

        List<Double> returns = new ArrayList<>();
        StockTickEvent previous = null;

        for (StockTickEvent current : window) {
            if (previous != null && previous.price() > 0) {
                double r = (current.price() - previous.price()) / previous.price();
                returns.add(r);
            }
            previous = current;
        }

        if (returns.isEmpty()) {
            return new VolatilityFeatures(0, 0, "LOW");
        }

        double mean = returns.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double variance = 0;
        for (double r : returns) {
            double diff = r - mean;
            variance += diff * diff;
        }
        variance /= returns.size();

        double stdDev = Math.sqrt(variance);

        String regime;
        if (stdDev < 0.002) {
            regime = "LOW";
        } else if (stdDev < 0.01) {
            regime = "MEDIUM";
        } else {
            regime = "HIGH";
        }

        return new VolatilityFeatures(
                mean,
                variance,
                regime
        );
    }
}
