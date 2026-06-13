package com.harsha.analysis_service.application.service.evaluator;

import com.harsha.analysis_service.application.service.evaluator.model.MarketEvaluationResult;
import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.contracts.events.analysis.TrendDirection;
import com.harsha.contracts.events.analysis.VolatilityRegime;
import org.springframework.stereotype.Component;

@Component
public class MarketStateEvaluator {
    public MarketEvaluationResult evaluate(
            StockFeatureSnapshot snapshot
    ) {
        double score = 0;

        //Trend Persistence
        score += snapshot.trend().persistence() * 25.0;

        //Momentum Persistence
        score += snapshot.momentum().momentumPersistence() * 25.0;

        //Efficiency Ratio
        score += snapshot.momentum().efficiencyRatio() * 20.0;

        //Acceleration
        double acceleration = Math.abs(snapshot.momentum().acceleration());

        score += Math.min(acceleration * 10.0, 15.0);

        //Moving Average Spread
        double maSpread = Math.abs(
                snapshot.movingAverage().sma5Tick() - snapshot.movingAverage().sma20Tick()
        );
        score += Math.min(maSpread, 10.0);

        //Clamp
        score = Math.min(score, 100.0);

        //Confidence
        double confidence = score / 100.0;

        //Market Regime
        MarketRegime marketRegime = determineMarketRegime(snapshot);

        return new MarketEvaluationResult(
                score,
                marketRegime,
                confidence
        );
    }

    private MarketRegime determineMarketRegime(
            StockFeatureSnapshot snapshot
    ) {
        TrendDirection trend =snapshot.trend().direction();

        VolatilityRegime volatility = snapshot.volatility().regime();

        if (trend.equals("UP")
                && volatility.equals("HIGH")) {
            return MarketRegime.STRONG_BULLISH;
        }

        if (trend.equals("DOWN")
                && volatility.equals("HIGH")) {
            return MarketRegime.STRONG_BEARISH;
        }

        if (volatility.equals("LOW")) {
            return MarketRegime.RANGE_BOUND;
        }
        return MarketRegime.NEUTRAL;
    }
}
