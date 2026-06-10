export interface InvestmentInsightSummary {
    id: string;
    symbol: string;
    action: string;
    opportunityScore: number;
    riskLevel: string;
    createdAt: string;
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