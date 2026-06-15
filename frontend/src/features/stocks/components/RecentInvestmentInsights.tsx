import Link from "next/link";
import { Sparkles, ArrowRight } from "lucide-react";

import InvestmentInsightTable from "@/features/investment-insights/components/InvestmentInsightTable";

import {
  PageResponse,
  InvestmentInsightSummary,
} from "../types/stock-overview";

interface Props {
  data: PageResponse<InvestmentInsightSummary>;
}

export default function RecentInvestmentInsights({ data }: Props) {
  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div
            className="w-6 h-6 rounded-md flex items-center justify-center"
            style={{ background: "#00D4FF18", color: "#00D4FF" }}
          >
            <Sparkles size={14} />
          </div>
          <h2 className="text-base font-semibold" style={{ color: "#E8EEF8" }}>
            Recent Investment Insights
          </h2>
        </div>

        <Link
          href="/investment-insights"
          className="flex items-center gap-1 text-xs transition-colors"
          style={{ color: "#00D4FF" }}
        >
          View More <ArrowRight size={12} />
        </Link>
      </div>

      <div className="rounded-xl overflow-hidden" style={{ border: "1px solid #1a2744" }}>
        <InvestmentInsightTable insights={data.content} />
      </div>
    </section>
  );
}