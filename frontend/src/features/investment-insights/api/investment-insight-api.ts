import axios from "axios";
import {
  InvestmentInsightSummary,
  PageResponse,
} from "../types/investment-insight.types";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_INV_INTELLIGENCE_API,
});

export async function getInvestmentInsights(
  page: number,
  size: number
): Promise<PageResponse<InvestmentInsightSummary>> {
  const response = await api.get(
    `/api/investment-insights/get-all?page=${page}&size=${size}`
  );

  return response.data;
}