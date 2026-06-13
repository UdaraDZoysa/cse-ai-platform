package com.harsha.strategy_service.application.detector;

import com.harsha.contracts.events.analysis.StockFeatureEvent;
import com.harsha.strategy_service.domain.model.detector.DetectorSignal;
import com.harsha.strategy_service.domain.model.detector.DetectorType;
import com.harsha.contracts.events.strategy.SignalDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MomentumDetector implements Detector {

    private static final Logger log = LoggerFactory.getLogger(MomentumDetector.class);


    @Override
    public DetectorSignal detect(
            StockFeatureEvent event
    ) {
        var momentum = event.momentum();

        log.info(
                """
                MOMENTUM INPUT_1
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        if (Double.isNaN(momentum.averageReturn())) {
            return DetectorSignal.invalid(
                    DetectorType.MOMENTUM
            );
        }

        log.info(
                """
                MOMENTUM INPUT_2
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        double directionalStrength =
                Math.abs(momentum.cumulativeReturn()) *
                        (1.0 + momentum.efficiencyRatio()) * momentum.momentumPersistence();

        log.info(
                """
                MOMENTUM INPUT_3
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        double accelerationBoost = Math.max(
                0,
                momentum.acceleration()
        );

        log.info(
                """
                MOMENTUM INPUT_4
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        double strength = directionalStrength + accelerationBoost;

        log.info(
                """
                MOMENTUM INPUT_5
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        double reliability = Math.min(
                1.0,
                momentum.efficiencyRatio() + momentum.momentumPersistence()
        );

        log.info(
                """
                MOMENTUM INPUT_6
                symbol={}
                cumulativeReturn={}
                efficiencyRatio={}
                momentumPersistence={}
                acceleration={}
                averageReturn={}
                """,
                event.symbol(),
                momentum.cumulativeReturn(),
                momentum.efficiencyRatio(),
                momentum.momentumPersistence(),
                momentum.acceleration(),
                momentum.averageReturn()
        );

        SignalDirection direction =
                momentum.cumulativeReturn() >= 0
                        ? SignalDirection.BULLISH
                        : SignalDirection.BEARISH;

        log.info(
                """
                MOMENTUM OUTPUT
                symbol={}
                direction={}
                directionalStrength={}
                accelerationBoost={}
                finalStrength={}
                reliability={}
                """,
                event.symbol(),
                direction,
                directionalStrength,
                accelerationBoost,
                strength,
                reliability
        );

        return new DetectorSignal(
                DetectorType.MOMENTUM,
                Math.abs(strength),
                reliability,
                direction,
                true
        );
    }
}
