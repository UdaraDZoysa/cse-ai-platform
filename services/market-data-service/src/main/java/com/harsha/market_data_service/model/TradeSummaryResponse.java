package com.harsha.market_data_service.model;

import java.util.List;

public record TradeSummaryResponse(
        List<StockData> reqTradeSummery
) {}
