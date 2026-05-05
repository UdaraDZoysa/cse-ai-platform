package com.harsha.analysis_service.handler;

import com.harsha.events.market.StockTickEvent;
import org.springframework.stereotype.Component;

@Component
public class StockTickEventHandler implements EventHandler<StockTickEvent> {

    @Override
    public String eventType() {
        return "STOCK_TICK_EVENT";
    }

    @Override
    public Class<StockTickEvent> eventClass() {
        return StockTickEvent.class;
    }

    @Override
    public void handle(StockTickEvent event) {
        //For now
        System.out.println(
                "Processing stock tick: " + event.symbol()
        );
    }
}
