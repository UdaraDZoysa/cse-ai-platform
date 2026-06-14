import { useQuery } from "@tanstack/react-query";
import { getInvestmentInsight } from "../api/investment-insight-detail-api";

export function useInvestmentInsightDetails(
  id: string
) {
  return useQuery({
    queryKey: ["investment-insight", id],
    queryFn: () => getInvestmentInsight(id),
    enabled: !!id,
  });
}