import axios from 'axios';
import {
    StockOverview, 
    PageResponse, 
    InvestmentInsightSummary, 
    MarketInsightHistoryResponse, 
    PriceHistory
} from '../types/stock-overview';

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_INV_INTELLIGENCE_API,
});

export async function getStockOverview(symbol: string): Promise<StockOverview> {
    const response = await api.get(`/api/stocks/${symbol}/overview`);
    return response.data;
}

export async function getInvestmentInsightSummaryForStock(
    symbol: string,
    page: number,
    size: number
): Promise<PageResponse<InvestmentInsightSummary>> {
    const response = await api.get(
        `/api/stocks/${symbol}/investment-insights?page=${page}&size=${size}`
    );
    return response.data;
}

export async function getMarketInsightHistoryForStock(
    symbol: string,
    page: number,
    size: number
): Promise<PageResponse<MarketInsightHistoryResponse>> {
    const response = await api.get(
        `/api/stocks/${symbol}/market-insights?page=${page}&size=${size}`
    );
    return response.data;
}

export async function getPriceHistoryForStock(
    symbol: string,
    days: number
): Promise<PriceHistory> {
    const response = await api.get(
        `/api/stocks/${symbol}/price-history?days=${days}`
    );
    return response.data;
}