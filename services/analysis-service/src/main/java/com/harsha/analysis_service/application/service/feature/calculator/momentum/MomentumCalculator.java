package com.harsha.analysis_service.application.service.feature.calculator.momentum;

import com.harsha.events.market.MomentumFeatures;
import com.harsha.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
public class MomentumCalculator {
    private final MomentumCalculatorHelper helperCalculator;

    public MomentumCalculator(
            MomentumCalculatorHelper helperCalculator) {
        this.helperCalculator = helperCalculator;
    }

    public MomentumFeatures calculate(
            Deque<StockTickEvent> window
    ) {
        if (window.size() < 3) {
            return MomentumFeatures.empty();
        }

        List<Double> returns = new ArrayList<>();
        List<Double> priceChanges = new ArrayList<>();

        StockTickEvent previous = null;

        double totalPathDistance = 0.0;
        int positiveMoves = 0;
        int negativeMoves = 0;

        double largestUpMove = Double.MIN_VALUE;
        double largestDownMove = Double.MAX_VALUE;

        for (StockTickEvent current : window) {
            if (previous != null) {
                double previousPrice = previous.price();
                double currentPrice = current.price();

                if (previousPrice <= 0) {
                    previous = current;
                    continue;
                }

                double delta = currentPrice - previousPrice;

                double returnValue = delta / previousPrice;

                returns.add(returnValue);
                priceChanges.add(delta);

                totalPathDistance += Math.abs(delta);

                if (delta > 0) {
                    positiveMoves++;
                    largestUpMove = Math.max(largestUpMove, delta);
                }

                if (delta < 0) {
                    negativeMoves++;
                    largestDownMove = Math.max(largestDownMove, delta);
                }
            }
            previous = current;
        }

        if (returns.isEmpty()) {
            return MomentumFeatures.empty();
        }

        double cumulativeReturn = helperCalculator.calculateCumulativeReturn(window);

        double averageReturn = helperCalculator.mean(returns);

        double returnStdDev = helperCalculator.standardDeviation(returns, averageReturn);

        double averageDelta = helperCalculator.mean(priceChanges);

        double acceleration = helperCalculator.calculateAcceleration(priceChanges);

        double positiveMoveRatio = (double) positiveMoves / returns.size();

        double negativeMoveRatio = (double) negativeMoves / returns.size();

        double momentumPersistence = Math.max(positiveMoveRatio, negativeMoveRatio);

        double efficiencyRatio = helperCalculator.calculateEfficiencyRatio(window, totalPathDistance);

        if (largestUpMove == Double.MIN_VALUE) {
            largestUpMove = 0;
        }

        if (largestDownMove == Double.MAX_VALUE) {
            largestDownMove = 0;
        }

        return new MomentumFeatures(
                cumulativeReturn,
                averageReturn,
                returnStdDev,
                averageDelta,
                acceleration,
                positiveMoveRatio,
                negativeMoveRatio,
                momentumPersistence,
                largestUpMove,
                largestDownMove,
                efficiencyRatio
        );

    }
}
