package com.harsha.analysis_service.application.service.feature.calculator.volatility;

import com.harsha.contracts.events.analysis.VolatilityRegime;
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
            return new VolatilityFeatures(
                    Double.NaN,
                    Double.NaN,
                    VolatilityRegime.UNKNOWN
            );
        }

        List<Double> returns = new ArrayList<>();

        StockTickEvent previous = null;

        for (StockTickEvent current : window) {
            if (previous != null &&
                    previous.price() > 0 &&
                        current.price() > 0
            ) {
                double logReturn = Math.log(
                        current.price() / previous.price()
                );

                returns.add(logReturn);
            }

            previous = current;
        }

        if (returns.size() < 2) {
            return new VolatilityFeatures(
                    Double.NaN,
                    Double.NaN,
                    VolatilityRegime.UNKNOWN
            );
        }

        double mean = returns.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(Double.NaN);

        double variance = 0;

        for (double value : returns) {
            double diff = value - mean;

            variance += diff * diff;
        }

        variance /= (returns.size() - 1);

        double stdDev = Math.sqrt(variance);

        VolatilityRegime regime;

        if (stdDev < 0.002) {
            regime = VolatilityRegime.LOW;

        } else if (stdDev < 0.01) {
            regime = VolatilityRegime.MEDIUM;

        } else if (stdDev < 0.03) {
            regime = VolatilityRegime.HIGH;

        } else {
            regime = VolatilityRegime.EXTREME;
        }

        return new VolatilityFeatures(
                stdDev,
                variance,
                regime
        );
    }
}
