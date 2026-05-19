package com.harsha.contracts.events.analysis;

public record MovingAverageFeatures(
        double sma5Tick,
        double sma20Tick,
        double ema5Tick,
        double ema20Tick
) {
}
