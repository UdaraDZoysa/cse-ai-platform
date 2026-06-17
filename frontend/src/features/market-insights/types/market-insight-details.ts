export interface MarketInsightDetails {
    id: string;
    symbol: string;
    companyName: string;        
    summary: string;
    reasoning: string;
    sentiment: string;
    importanceScore: number;
    confidenceScore: number;
    persistenceScore: number;
    generatedBy: string;
    generatedAt: string;
}