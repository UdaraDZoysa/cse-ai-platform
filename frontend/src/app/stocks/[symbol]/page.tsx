"use client";

import { useParams, useRouter } from "next/navigation";
import { useState } from "react";
import { ChevronLeft, Activity, ArrowLeft } from "lucide-react";
import Link from "next/link";

import {
  useStockOverview,
  usePriceHistoryForStock,
  useInvestmentInsightSummaryForStock,
  useMarketInsightHistoryForStock,
} from "@/features/stocks/hooks/useStockOverview";

import StockOverviewCard from "@/features/stocks/components/StockOverviewCard";
import PriceHistoryChart from "@/features/stocks/components/PriceHistoryChart";
import RecentInvestmentInsights from "@/features/stocks/components/RecentInvestmentInsights";
import RecentMarketInsights from "@/features/stocks/components/RecentMarketInsights";

const RANGE_OPTIONS = [1, 7, 30, 90, 365];

export default function StockPage() {
  const params = useParams();
  const router = useRouter();
  const symbol = params.symbol as string;

  const overview = useStockOverview(symbol);

  const [days, setDays] = useState(30);

  const priceHistory = usePriceHistoryForStock(symbol, days);
  const investmentInsights = useInvestmentInsightSummaryForStock(symbol, 0, 5);
  const marketInsights = useMarketInsightHistoryForStock(symbol, 0, 5);

  const loading =
    overview.isLoading ||
    priceHistory.isLoading ||
    investmentInsights.isLoading ||
    marketInsights.isLoading;

  if (loading) {
    return (
      <div
        className="min-h-screen flex items-center justify-center"
        style={{ background: "#0A0E1A" }}
      >
        <div className="flex flex-col items-center gap-3">
          <Activity
            size={24}
            className="animate-pulse"
            style={{ color: "#00D4FF" }}
          />
          <span
            className="text-sm"
            style={{
              fontFamily: "'JetBrains Mono', monospace",
              color: "#6B7FA3",
            }}
          >
            Loading stock data…
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen" style={{ background: "#0A0E1A" }}>
      {/* ── Top Bar ── */}
      <div
        className="sticky top-0 z-10 px-8 py-4"
        style={{
          background: "#0A0E1Aee",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid #1a2744",
        }}
      >
        <div className="max-w-6xl mx-auto">
          <Link
            href="/investment-insights"
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: "#6B7FA3" }}
            onMouseEnter={(e) => (e.currentTarget.style.color = "#E8EEF8")}
            onMouseLeave={(e) => (e.currentTarget.style.color = "#6B7FA3")}
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
                  fontFamily: "'JetBrains Mono', monospace",
                  color: active ? "#00D4FF" : "#6B7FA3",
                  background: active ? "#00D4FF15" : "transparent",
                  border: active ? "1px solid #00D4FF33" : "1px solid #1a2744",
                }}
                onMouseEnter={(e) => {
                  if (!active) e.currentTarget.style.color = "#E8EEF8";
                }}
                onMouseLeave={(e) => {
                  if (!active) e.currentTarget.style.color = "#6B7FA3";
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

        {/* Recent insights */}
        <RecentInvestmentInsights 
          symbol={symbol}
          data={investmentInsights.data!} />
        <RecentMarketInsights 
          symbol={symbol}
          data={marketInsights.data!} />
      </div>
    </div>
  );
}