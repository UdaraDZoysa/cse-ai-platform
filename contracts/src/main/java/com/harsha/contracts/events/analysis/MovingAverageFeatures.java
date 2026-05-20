package com.harsha.contracts.events.analysis;

public record MovingAverageFeatures(
        Double sma5Tick,
        Double sma20Tick,
        Double ema5Tick,
        Double ema20Tick
) {
}
