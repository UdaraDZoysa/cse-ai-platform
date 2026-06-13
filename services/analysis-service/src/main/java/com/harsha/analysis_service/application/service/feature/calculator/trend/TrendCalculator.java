package com.harsha.analysis_service.application.service.feature.calculator.trend;

import com.harsha.analysis_service.application.service.feature.model.MoveDirection;
import com.harsha.contracts.events.analysis.TrendDirection;
import com.harsha.contracts.events.market.StockTickEvent;
import com.harsha.contracts.events.analysis.TrendFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class TrendCalculator {
    private static final Logger log = LoggerFactory.getLogger(TrendCalculator.class);


    public TrendFeatures calculate(
            Deque<StockTickEvent> window
    ) {
        int upward = 0;
        int downward = 0;

        int longestSequence = 0;
        int currentSequence = 0;
        MoveDirection previousMove = null;

        StockTickEvent previous = null;

        for (StockTickEvent current : window) {

            if (previous != null) {

                MoveDirection currentMove = MoveDirection.FLAT;

                if (current.price() > previous.price()) {
                    upward++;
                    currentMove = MoveDirection.UP;

                } else if (current.price() < previous.price()) {
                    downward++;
                    currentMove = MoveDirection.DOWN;
                }

                if (currentMove == MoveDirection.FLAT) {
                    previous = current;
                    continue;
                }

                if (currentMove.equals(previousMove)) {
                    currentSequence++;

                } else {
                    currentSequence = 1;
                }

                longestSequence = Math.max(
                        longestSequence,
                        currentSequence
                );

                previousMove = currentMove;
            }

            previous = current;
        }

        int totalMoves = upward + downward;

        double upwardRatio =
                (double) upward / totalMoves;

        double downwardRatio =
                (double) downward / totalMoves;

        double persistence =
                (double) longestSequence / totalMoves;

        double imbalance = Math.abs(
                upwardRatio - downwardRatio
        );


        TrendDirection direction;

        if (imbalance < 0.05) {
            direction = TrendDirection.SIDEWAYS;
        } else if (upwardRatio > downwardRatio) {
            direction = TrendDirection.BULLISH;
        } else {
            direction = TrendDirection.BEARISH;
        }

        log.info(
                "TREND CALCULATOR imbalance={} direction={}",
                imbalance,
                direction
        );

        return new TrendFeatures(
                upwardRatio,
                downwardRatio,
                persistence,
                direction
        );
    }
}
