package com.harsha.analysis_service.application.service.feature;

import com.harsha.analysis_service.application.service.feature.model.StockFeatureSnapshot;
import com.harsha.analysis_service.application.service.feature.pipeline.FeaturePipeline;
import com.harsha.analysis_service.application.service.feature.store.RollingWindowStore;
import com.harsha.contracts.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

import java.util.Deque;

@Component
public class FeatureExtractor {
    private final RollingWindowStore rollingWindowStore;
    private final FeaturePipeline featurePipeline;

    public FeatureExtractor(
            RollingWindowStore rollingWindowStore,
            FeaturePipeline featurePipeline
    ) {
        this.rollingWindowStore = rollingWindowStore;
        this.featurePipeline = featurePipeline;
    }

    public StockFeatureSnapshot extract(
            StockTickEvent tick
    ) {
        Deque<StockTickEvent> window = rollingWindowStore.addTick(tick);

        return featurePipeline.build(
                tick.symbol(),
                tick.lastTradedTime(),
                window
        );
    }

    public Deque<StockTickEvent> currentWindow(
            String symbol
    ) {
        return rollingWindowStore.currentWindow(symbol);
    }

}
