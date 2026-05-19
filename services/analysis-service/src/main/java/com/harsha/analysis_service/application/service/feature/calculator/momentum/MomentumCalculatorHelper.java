package com.harsha.analysis_service.application.service.feature.calculator.momentum;

import com.harsha.analysis_service.application.service.feature.model.MoveDirection;
import com.harsha.contracts.events.market.StockTickEvent;
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

        if (first <= 0 || last <= 0) {
            return 0;
        }

        return Math.log(last / first);
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

        if (values.size() < 2) {
            return 0;
        }

        double variance = 0;

        for (double value : values) {
            double diff = value - mean;
            variance += diff * diff;
        }

        variance /= (values.size() - 1);

        return Math.sqrt(variance);
    }

    double calculateReturnAcceleration(
            List<Double> returns
    ) {

        if (returns.size() < 2) {
            return 0;
        }

        double totalAcceleration = 0;

        for (int i = 1; i < returns.size(); i++) {

            totalAcceleration += returns.get(i) - returns.get(i - 1);
        }

        return totalAcceleration / (returns.size() - 1);
    }

    double calculateEfficiencyRatio(
            Deque<StockTickEvent> window,
            double totalPathDistance
    ) {

        if (totalPathDistance == 0) {
            return 0;
        }

        double first = window.getFirst().price();

        double last = window.getLast().price();

        double netDistance = Math.abs(
                Math.log(last / first)
        );

        return netDistance / totalPathDistance;
    }

    double calculateDirectionalPersistence(
            List<Double> returns
    ) {

        if (returns.isEmpty()) {
            return 0;
        }

        int longestSequence = 0;

        int currentSequence = 0;

        MoveDirection previousDirection = MoveDirection.FLAT;

        for (double value : returns) {

            MoveDirection currentDirection;

            if (value > 0) {
                currentDirection = MoveDirection.UP;

            } else if (value < 0) {
                currentDirection = MoveDirection.DOWN;

            } else {
                currentDirection = MoveDirection.FLAT;
            }

            if (currentDirection == MoveDirection.FLAT) {
                continue;
            }

            if (currentDirection == previousDirection) {
                currentSequence++;

            } else {
                currentSequence = 1;
            }

            longestSequence = Math.max(
                    longestSequence,
                    currentSequence
            );

            previousDirection = currentDirection;
        }

        return (double) longestSequence / returns.size();
    }
}
