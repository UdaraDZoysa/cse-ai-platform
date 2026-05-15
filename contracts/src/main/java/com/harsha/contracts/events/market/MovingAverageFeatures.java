package com.harsha.contracts.events.market;

public record MovingAverageFeatures(
        double sma5Tick,
        double sma20Tick,
        double ema5Tick,
        double ema20Tick
) {
}
