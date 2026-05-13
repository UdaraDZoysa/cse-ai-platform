package com.harsha.analysis_service.application.service.feature.calculator.momentum;

import com.harsha.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.List;

@Component
public class MomentumCalculatorHelper {
    double calculateCumulativeReturn(
            Deque<StockTickEvent> window
    ) {
        double first = window.getFirst().price();

        double last = window.getLast().price();

        if (first <= 0) {
            return 0;
        }

        return (last - first) / first;
    }

    double mean(
            List<Double> values
    ) {

        if (values.isEmpty()) {
            return 0;
        }

        double sum = 0;

        for (double value : values) {
            sum += value;
        }

        return sum / values.size();
    }

    double standardDeviation(
            List<Double> values,
            double mean
    ) {

        if (values.isEmpty()) {
            return 0;
        }

        double variance = 0;

        for (double value : values) {
            double diff = value - mean;
            variance += diff * diff;
        }

        variance /= values.size();

        return Math.sqrt(variance);
    }

    double calculateAcceleration(
            List<Double> priceChanges
    ) {

        if (priceChanges.size() < 2) {
            return 0;
        }

        double totalAcceleration = 0;

        for (int i = 1; i < priceChanges.size(); i++) {

            totalAcceleration += priceChanges.get(i) - priceChanges.get(i - 1);
        }

        return totalAcceleration / (priceChanges.size() - 1);
    }

    double calculateEfficiencyRatio(
            Deque<StockTickEvent> window,
            double totalPathDistance
    ) {

        if (totalPathDistance == 0) {
            return 0;
        }

        double netDistance =
                Math.abs(
                        window.getLast().price() - window.getFirst().price()
                );

        return netDistance / totalPathDistance;
    }
}
