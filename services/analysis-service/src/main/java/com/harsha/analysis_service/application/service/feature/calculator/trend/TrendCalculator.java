package com.harsha.analysis_service.application.service.feature.calculator.trend;

import com.harsha.contracts.events.market.StockTickEvent;
import com. harsha. contracts. events. market. TrendFeatures;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class TrendCalculator {

    public TrendFeatures calculate(
            Deque<StockTickEvent> window
    ) {
        if (window.size() < 2) {
            return new TrendFeatures(
                    0,
                    0,
                    0,
                    "FLAT"
            );
        }

        int upward = 0;
        int downward = 0;

        StockTickEvent previous = null;

        for (StockTickEvent current : window) {
            if (previous != null) {
                if (current.price() > previous.price()) {
                    upward++;
                }
                if (current.price() < previous.price()) {
                    downward++;
                }
            }
            previous = current;
        }

        int totalMoves = upward + downward;

        if (totalMoves == 0) {
            return new TrendFeatures(
                    0,
                    0,
                    0,
                    "FLAT"
            );
        }

        double upwardRatio =
                (double) upward / totalMoves;

        double downwardRatio =
                (double) downward / totalMoves;

        String direction =
                upwardRatio >= downwardRatio ? "UP" : "DOWN";

        double persistence =
                Math.max(upwardRatio, downwardRatio);

        return new TrendFeatures(
                upwardRatio,
                downwardRatio,
                persistence,
                direction
        );
    }
}
