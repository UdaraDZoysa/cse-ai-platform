"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import { ChevronLeft, Activity } from "lucide-react";
import Link from "next/link";

import {
  useStockOverview,
  usePriceHistoryForStock,
  useInvestmentInsightSummaryForStock,
  useMarketInsightHistoryForStock,
  useMarketNarrativeForStock,
} from "@/features/stocks/hooks/useStockOverview";

import StockOverviewCard from "@/features/stocks/components/StockOverviewCard";
import PriceHistoryChart from "@/features/stocks/components/PriceHistoryChart";
import RecentInvestmentInsights from "@/features/stocks/components/RecentInvestmentInsights";
import RecentMarketInsights from "@/features/stocks/components/RecentMarketInsights";
import MarketNarrativeCard from "@/features/stocks/components/MarketNarrativeCard";
import { colors, fonts, withAlpha } from "@/theme/theme";

const RANGE_OPTIONS = [1, 7, 30, 90, 365];

export default function StockPage() {
  const params = useParams();
  const symbol = params.symbol as string;

  const overview = useStockOverview(symbol);

  const [days, setDays] = useState(30);

  const priceHistory = usePriceHistoryForStock(symbol, days);
  const investmentInsights = useInvestmentInsightSummaryForStock(symbol, 0, 5);
  const marketInsights = useMarketInsightHistoryForStock(symbol, 0, 5);
  const narrative = useMarketNarrativeForStock(symbol);

  const loading =
    overview.isLoading ||
    priceHistory.isLoading ||
    investmentInsights.isLoading ||
    marketInsights.isLoading ||
    narrative.isLoading;

  if (loading) {
    return (
      <div
        className="min-h-screen flex items-center justify-center"
        style={{ background: colors.bgPage }}
      >
        <div className="flex flex-col items-center gap-3">
          <Activity size={24} className="animate-pulse" style={{ color: colors.accent }} />
          <span
            className="text-sm"
            style={{ fontFamily: fonts.mono, color: colors.textMuted }}
          >
            Loading stock data…
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen" style={{ background: colors.bgPage }}>
      {/* ── Top Bar ── */}
      <div
        className="sticky top-0 z-10 px-8 py-4"
        style={{
          background: withAlpha("page", 0.93),
          backdropFilter: "blur(12px)",
          borderBottom: `1px solid ${colors.border}`,
        }}
      >
        <div className="max-w-6xl mx-auto">
          <Link
            href="/investment-insights"
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: colors.textMuted }}
            onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
            onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
          >
            <ChevronLeft size={14} />
            Back to Insights
          </Link>
        </div>
      </div>

      <div className="px-8 py-8 max-w-6xl mx-auto space-y-6">
        {/* Stock overview hero */}
        <StockOverviewCard overview={overview.data!} />

        {/* Time range selector */}
        <div className="flex items-center gap-2">
          {RANGE_OPTIONS.map((value) => {
            const active = days === value;
            return (
              <button
                key={value}
                onClick={() => setDays(value)}
                className="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all"
                style={{
                  fontFamily: fonts.mono,
                  color: active ? colors.accent : colors.textMuted,
                  background: active ? withAlpha("accent", 0.08) : "transparent",
                  border: active
                    ? `1px solid ${withAlpha("accent", 0.2)}`
                    : `1px solid ${colors.border}`,
                }}
                onMouseEnter={(e) => {
                  if (!active) e.currentTarget.style.color = colors.textPrimary;
                }}
                onMouseLeave={(e) => {
                  if (!active) e.currentTarget.style.color = colors.textMuted;
                }}
              >
                {value === 365 ? "1Y" : `${value}D`}
              </button>
            );
          })}
        </div>

        {/* Price history chart */}
        <PriceHistoryChart
          history={priceHistory.data!}
          days={days}
          onDaysChange={setDays}
        />

        {/* Market Narratives */}
        <MarketNarrativeCard narrative={narrative.data!} />

        {/* Recent Investment Insights */}
        <RecentInvestmentInsights symbol={symbol} data={investmentInsights.data!} />

        {/* Recent Market Insights */}
        <RecentMarketInsights symbol={symbol} data={marketInsights.data!} />
      </div>
    </div>
  );
}
