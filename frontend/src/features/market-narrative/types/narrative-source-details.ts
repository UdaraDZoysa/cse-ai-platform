export interface MarketNarrativeSource {
    symbol: string;
    companyName: string;
    title: string;
    sourceUrl: string;
    publishedDate: string;
}

export interface NarrativeDetailsResponse {
    marketNarrativeSource: MarketNarrativeSource[];
}