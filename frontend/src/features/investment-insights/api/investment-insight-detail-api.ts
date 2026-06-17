import axios from "axios";
import { InvestmentInsightDetail } from "../types/investment-insight-detail";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_INV_BFF_API,
});

export async function getInvestmentInsight(
  id: string
): Promise<InvestmentInsightDetail> {

  const response = await api.get(
    `/api/investment-insights/${id}`
  );

  return response.data;
}