import { useQuery } from "@tanstack/react-query";
import { getInvestmentInsights } from "../api/investment-insight-api";

export function useInvestmentInsights(
  page: number,
  size: number
) {
  return useQuery({
    queryKey: ["investment-insights", page, size],
    queryFn: () => getInvestmentInsights(page, size),
  });
}