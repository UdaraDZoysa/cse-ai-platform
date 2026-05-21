package com.harsha.strategy_service.application.regime;

import com.harsha.strategy_service.domain.model.NormalizedFeatureSet;
import com.harsha.strategy_service.domain.model.RegimeEvaluation;

public record RegimeContext(
        boolean statisticalReady,
        NormalizedFeatureSet normalizedFeatures,
        RegimeEvaluation regimeEvaluation
) {
}
