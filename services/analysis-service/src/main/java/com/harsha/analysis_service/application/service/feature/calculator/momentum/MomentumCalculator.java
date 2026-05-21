package com.harsha.analysis_service.application.service.feature.calculator.momentum;

import com.harsha.contracts.events.analysis.MomentumFeatures;
import com.harsha.contracts.events.market.StockTickEvent;
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
        List<Double> returns = new ArrayList<>();

        StockTickEvent previous = null;

        double totalPathDistance = 0.0;
        int positiveMoves = 0;
        int negativeMoves = 0;

        double largestPositiveReturn = Double.NEGATIVE_INFINITY;
        double largestNegativeReturn = Double.POSITIVE_INFINITY;

        for (StockTickEvent current : window) {
            if (previous != null) {
                double previousPrice = previous.price();
                double currentPrice = current.price();

                if (previousPrice <= 0) {
                    previous = current;
                    continue;
                }

                double returnValue = Math.log(
                        currentPrice / previousPrice
                );

                returns.add(returnValue);

                totalPathDistance += Math.abs(returnValue);

                if (returnValue > 0) {
                    positiveMoves++;
                    largestPositiveReturn = Math.max(largestPositiveReturn, returnValue);
                }

                if (returnValue < 0) {
                    negativeMoves++;
                    largestNegativeReturn = Math.min(largestNegativeReturn, returnValue);
                }
            }
            previous = current;
        }

        double cumulativeReturn = helperCalculator.calculateCumulativeReturn( window );

        double averageReturn = helperCalculator.mean( returns );

        double returnStdDev = helperCalculator.standardDeviation( returns, averageReturn );

        double acceleration = helperCalculator.calculateReturnAcceleration( returns );

        double positiveMoveRatio =
                (double) positiveMoves / returns.size();

        double negativeMoveRatio =
                (double) negativeMoves / returns.size();

        double momentumPersistence = helperCalculator.calculateDirectionalPersistence( returns );

        double efficiencyRatio = helperCalculator.calculateEfficiencyRatio( window, totalPathDistance );

        if (largestPositiveReturn == Double.NEGATIVE_INFINITY) {
            largestPositiveReturn = 0;
        }

        if (largestNegativeReturn == Double.POSITIVE_INFINITY) {
            largestNegativeReturn = 0;
        }

        return new MomentumFeatures(
                cumulativeReturn,
                averageReturn,
                returnStdDev,
                acceleration,
                positiveMoveRatio,
                negativeMoveRatio,
                momentumPersistence,
                largestPositiveReturn,
                largestNegativeReturn,
                efficiencyRatio
        );

    }
}
