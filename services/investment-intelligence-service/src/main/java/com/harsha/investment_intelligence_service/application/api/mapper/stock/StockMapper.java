package com.harsha.investment_intelligence_service.application.api.mapper.stock;

import com.harsha.investment_intelligence_service.application.api.dto.stock.MarketInsightHistoryResponse;
import com.harsha.investment_intelligence_service.application.api.dto.stock.PriceHistoryPoint;
import com.harsha.investment_intelligence_service.application.api.dto.stock.PriceHistoryProjection;
import com.harsha.investment_intelligence_service.application.api.dto.stock.StockOverviewResponse;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketInsightHistoryEntity;
import com.harsha.investment_intelligence_service.infrastructure.storage.entity.MarketSnapshotHistoryEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class StockMapper {
    public StockOverviewResponse toOverviewResponse (
            MarketSnapshotHistoryEntity entity
    ){
        return new StockOverviewResponse(
                entity.getSymbol(),
                entity.getCompany(),
                entity.getPrice(),
                entity.getPercentageChange(),
                entity.getPreviousClose(),
                entity.getOpen(),
                entity.getHigh(),
                entity.getLow(),
                entity.getShareVolume(),
                entity.getTradeVolume(),
                entity.getTurnover(),
                entity.getMarketCap(),
                Instant.ofEpochMilli(entity.getLastTradedTime())
        );
    }

    public PriceHistoryPoint toPriceHistoryPoint (
            PriceHistoryProjection projection
    ) {
        return new PriceHistoryPoint(
                Instant.ofEpochMilli(projection.occurredAt()),
                projection.price()
        );
    }

    public MarketInsightHistoryResponse toMarketInsightHistoryResponse (
            MarketInsightHistoryEntity entity
    ) {
        return new MarketInsightHistoryResponse(
                entity.getId().toString(),
                entity.getSymbol(),
                entity.getCompany(),
                entity.getSummary(),
                entity.getSentiment().name(),
                entity.getImportanceScore(),
                Instant.ofEpochMilli(entity.getOccurredAt())
        );
    }
}
