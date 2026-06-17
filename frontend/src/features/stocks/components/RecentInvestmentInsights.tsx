import Link from "next/link";
import { Sparkles, ArrowRight } from "lucide-react";

import InvestmentInsightTable from "@/features/investment-insights/components/InvestmentInsightTable";
import { colors, withAlpha } from "@/theme/theme";

import {
  PageResponse,
  InvestmentInsightSummary,
} from "../types/stock-overview";

interface Props {
  symbol: string;
  data: PageResponse<InvestmentInsightSummary>;
}

export default function RecentInvestmentInsights({ symbol, data }: Props) {
  return (
    <section>
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div
            className="w-6 h-6 rounded-md flex items-center justify-center"
            style={{ background: withAlpha("accent", 0.1), color: colors.accent }}
          >
            <Sparkles size={14} />
          </div>
          <h2 className="text-base font-semibold" style={{ color: colors.textPrimary }}>
            Recent Investment Insights
          </h2>
        </div>

        <Link
          href={`/stocks/${symbol}/investment-insights`}
          className="flex items-center gap-1 text-xs transition-colors"
          style={{ color: colors.accent }}
        >
          View More <ArrowRight size={12} />
        </Link>
      </div>

      <div className="rounded-xl overflow-hidden" style={{ border: `1px solid ${colors.border}` }}>
        <InvestmentInsightTable insights={data.content} />
      </div>
    </section>
  );
}
