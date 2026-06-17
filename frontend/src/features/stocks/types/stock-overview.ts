export interface StockOverview {
    symbol: string;
    companyName: string;        
    currentPrice: number;
    percentageChange: number;
    previousClose: number;
    open: number;
    high: number;
    low: number;
    shareVolume: number;
    tradeVolume: number;
    turnover: number;
    marketCap: number;
    lastUpdatedAt: string;
 }

 export interface PageResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
 }

 export interface PriceHistoryPoint {
    timestamp: string;
    price: number;
 }
 
 export interface PriceHistory {
    points: PriceHistoryPoint[];
 }

 export interface MarketInsightHistoryResponse {
    id: string;
    symbol: string;
    companyName: string;        
    summary: string;
    sentiment: string;
    importanceScore: number;
    generatedAt: string;
 }

 export interface InvestmentInsightSummary {
    id: string;
    symbol: string;
    companyName: string;        
    action: string;
    opportunityScore: number;
    riskLevel: string;
    createdAt: string;
}
export interface MarketNarratives {
   id: string;
   symbol: string;
   companyName: string;        
   summary: string;
   generatedAt: string;
}
