package com.harsha.strategy_service.application.regime;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.application.evaluator.FeatureNormalizer;
import com.harsha.strategy_service.application.statistics.StatisticalReadinessEvaluator;
import com.harsha.strategy_service.domain.model.*;
import org.springframework.stereotype.Service;

@Service
public class RegimeContextService {
    private final StatisticalReadinessEvaluator readinessEvaluator;
    private final FeatureNormalizer featureNormalizer;
    private final MarketRegimeDetector regimeDetector;
    private final RegimeEvaluationFactory evaluationFactory;

    public RegimeContextService(
            StatisticalReadinessEvaluator readinessEvaluator,
            FeatureNormalizer featureNormalizer,
            MarketRegimeDetector regimeDetector,
            RegimeEvaluationFactory evaluationFactory
    ) {
        this.readinessEvaluator = readinessEvaluator;
        this.featureNormalizer = featureNormalizer;
        this.regimeDetector = regimeDetector;
        this.evaluationFactory = evaluationFactory;
    }

    public RegimeContext resolve(
            StockFeatureEvent event,
            SymbolStatisticsState statistics
    ) {
        boolean statisticalReady = readinessEvaluator.ready(statistics);


        //Warmup fallback
        if (!statisticalReady) {
            return new RegimeContext(
                    false,
                    null,
                    defaultEvaluation()
            );
        }

        //Normalize features
        NormalizedFeatureSet normalized =
                featureNormalizer.normalize(
                        event,
                        statistics
                );

        //Detect regime
        RegimeState regimeState =
                regimeDetector.detect(normalized);

        //Build evaluation
        RegimeEvaluation evaluation =
                evaluationFactory.evaluate(regimeState);

        return new RegimeContext(
                true,
                normalized,
                evaluation
        );
    }

    private RegimeEvaluation defaultEvaluation() {

        return new RegimeEvaluation(
                new RegimeState(
                        MarketRegime.SIDEWAYS,
                        0.5
                ),
                1.0,
                1.0,
                1.0,
                1.0
        );
    }
}
