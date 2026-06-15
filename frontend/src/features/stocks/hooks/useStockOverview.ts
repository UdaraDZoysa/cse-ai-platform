import { useQuery } from '@tanstack/react-query';
import { 
    getStockOverview, 
    getPriceHistoryForStock, 
    getMarketInsightHistoryForStock, 
    getInvestmentInsightSummaryForStock 
} from '../api/stock-overview-api';

export function useStockOverview(
    symbol: string
) {
    return useQuery({
        queryKey: ['stock-overview', symbol],
        queryFn: () => getStockOverview(symbol),
        enabled: !!symbol,
    });
}

export function usePriceHistoryForStock(
    symbol: string,
    days: number
) {
    return useQuery({
        queryKey: ['price-history', symbol, days],
        queryFn: () => getPriceHistoryForStock(symbol, days),
        enabled: !!symbol,
    });
}

export function useMarketInsightHistoryForStock(
    symbol: string,
    page: number,
    size: number
) {
    return useQuery({
        queryKey: ['market-insight-history', symbol, page, size],
        queryFn: () => getMarketInsightHistoryForStock(symbol, page, size),
        enabled: !!symbol,
    });
}

export function useInvestmentInsightSummaryForStock(
    symbol: string,
    page: number,
    size: number
) {
    return useQuery({
        queryKey: ['investment-insight-summary', symbol, page, size],
        queryFn: () => getInvestmentInsightSummaryForStock(symbol, page, size),
        enabled: !!symbol,
    });
}