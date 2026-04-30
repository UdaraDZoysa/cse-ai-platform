package com.harsha.market_data_service.signal;

import com.harsha.events.market.Signal;
import com.harsha.events.market.StockFeatures;
import org.springframework.stereotype.Component;

@Component
public class SignalEngine {
    public Signal evaluates(StockFeatures stockFeatures) {
        //--------TREND SIGNAL--------
        if (Math.abs(stockFeatures.priceChangePercent()) >= 0.2 &&
            stockFeatures.volatility() >= 0.1 &&
            stockFeatures.volumeSpike() &&
            stockFeatures.trendStrength() >=3) {

            return new Signal(
                    stockFeatures.symbol(),
                    "TREND",
                    stockFeatures.trend(),
                    stockFeatures
            );
        }

        //--------BREAKOUT SIGNAL--------
        if (Math.abs(stockFeatures.priceChangePercent()) >= 0.5 &&
                stockFeatures.volumeSpike()) {

            return new Signal(
                    stockFeatures.symbol(),
                    "BREAKOUT",
                    stockFeatures.trend(),
                    stockFeatures
            );
        }
        return null;
    }
}
