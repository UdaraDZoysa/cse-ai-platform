export interface WatchListResponse {
  id: string;
  symbols: string[];
  updatedAt: string;
}

export interface StockLookup {
  symbol: string;
  companyName: string;
}

export interface StockLookupResponse {
  stockLookups: StockLookup[];
}
