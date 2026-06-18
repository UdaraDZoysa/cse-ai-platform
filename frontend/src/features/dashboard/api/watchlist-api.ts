import axios from "axios";

import { StockLookupResponse, WatchListResponse } from "../types/watchlist";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_INV_BFF_API,
});

export async function getWatchList(): Promise<WatchListResponse> {
  const response = await api.get("/api/watchlist");
  return response.data;
}

export async function updateWatchList(symbols: string[]): Promise<void> {
  await api.post("/api/watchlist", symbols);
}

export async function getStockLookup(): Promise<StockLookupResponse> {
  const response = await api.get("/api/stocks/stock-lookup");
  return response.data;
}
