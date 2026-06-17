import axios from "axios";
import { NarrativeDetailsResponse } from "../types/narrative-source-details";

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_INV_BFF_API,
});

export async function getNarrativeDetails(
    id: string
): Promise<NarrativeDetailsResponse> {
    const response = await api.get(
        `/api/market-narrative/${id}`  
    );
    return response.data;
}