import axios from "axios";
import { MarketInsightDetails } from "../types/market-insight-details";

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_INV_BFF_API,
});

export async function getMarketInsightDetails(
    id: string
): Promise<MarketInsightDetails> {
    const response = await api.get(
        `api/market-insight/${id}`
    );
    return response.data;
}   