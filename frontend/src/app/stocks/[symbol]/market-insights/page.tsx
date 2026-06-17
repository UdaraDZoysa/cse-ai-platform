"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import Link from "next/link";
import {
  ChevronLeft,
  ChevronRight,
  BarChart3,
  Activity,
  AlertCircle,
  Inbox,
} from "lucide-react";

import MarketInsightTable from "@/features/stocks/components/MarketInsightTable";
import { useMarketInsightHistoryForStock } from "@/features/stocks/hooks/useStockOverview";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

export default function StockMarketInsightsPage() {
  const params = useParams();
  const symbol = params.symbol as string;

  const [page, setPage] = useState(0);

  const { data, isLoading, error } = useMarketInsightHistoryForStock(
    symbol,
    page,
    20
  );

  const pageNumbers = data
    ? Array.from({ length: data.totalPages }, (_, index) => index).filter(
        (p) => p === 0 || p === data.totalPages - 1 || Math.abs(p - page) <= 2
      )
    : [];
  
  // Default to symbol if company name is not available
  const companyName = data?.content?.[0]?.companyName || symbol; 

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
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <Link
            href={`/stocks/${symbol}`}
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: colors.textMuted }}
            onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
            onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
          >
            <ChevronLeft size={14} />
            Back to Stock
          </Link>

          {data && (
            <div
              className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-lg"
              style={{
                fontFamily: fonts.mono,
                color: colors.textMuted,
                background: colors.bgSurface,
                border: `1px solid ${colors.border}`,
              }}
            >
              <BarChart3 size={11} style={{ color: colors.accent }} />
              {data.totalElements} market insights
            </div>
          )}
        </div>
      </div>

      <div className="px-8 py-8 max-w-7xl mx-auto space-y-6">
        {/* ── Page Header ── */}
        <div
          className="rounded-2xl px-7 py-6"
          style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
        >
          <div className="flex items-center gap-3 mb-2">
            <div
              className="w-8 h-8 rounded-lg flex items-center justify-center"
              style={{
                background: withAlpha("accent", 0.1),
                border: `1px solid ${withAlpha("accent", 0.2)}`,
              }}
            >
              <BarChart3 size={16} style={{ color: colors.accent }} />
            </div>
            <h1 className="text-2xl font-bold" style={{ color: colors.textPrimary }}>
              Market Insights
            </h1>
            <span
              className="text-xs px-2 py-0.5 rounded-md font-semibold"
              style={{
                fontFamily: fonts.mono,
                color: colors.accent,
                background: withAlpha("accent", 0.08),
                border: `1px solid ${withAlpha("accent", 0.2)}`,
              }}
            >
              {symbol}
            </span>
          </div>
          <p className="text-sm" style={{ color: colors.textMuted }}>
            AI analysis of market behavior and sentiment for {companyName}
          </p>
        </div>

        {/* ── Loading ── */}
        {isLoading && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <Activity size={24} className="animate-pulse" style={{ color: colors.accent }} />
            <span
              className="text-sm"
              style={{ fontFamily: fonts.mono, color: colors.textMuted }}
            >
              Loading market insights…
            </span>
          </div>
        )}

        {/* ── Error ── */}
        {error && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{ background: colors.bgSurface, border: `1px solid ${withAlpha("negative", 0.2)}` }}
          >
            <AlertCircle size={24} style={{ color: colors.negative }} />
            <span className="text-sm" style={{ color: colors.textMuted }}>
              Failed to load market insights.
            </span>
          </div>
        )}

        {/* ── Empty ── */}
        {!isLoading && !error && data && data.content.length === 0 && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <Inbox size={24} style={{ color: colors.textFaint }} />
            <span className="text-sm" style={{ color: colors.textMuted }}>
              No market insights found for {symbol}.
            </span>
          </div>
        )}

        {/* ── Data ── */}
        {!isLoading && !error && data && data.content.length > 0 && (
          <>
            {/* Table container */}
            <div
              className="rounded-xl overflow-hidden"
              style={{ border: `1px solid ${colors.border}` }}
            >
              <MarketInsightTable insights={data.content} />
            </div>

            {/* ── Pagination footer ── */}
            <div
              className="rounded-xl px-5 py-4 flex items-center justify-between"
              style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
            >
              <span
                className="text-xs"
                style={{ fontFamily: fonts.mono, color: colors.textFaint }}
              >
                Page <span style={{ color: colors.textMuted }}>{data.number + 1}</span> of{" "}
                <span style={{ color: colors.textMuted }}>{data.totalPages}</span>
                {"  ·  "}
                <span style={{ color: colors.textMuted }}>{data.totalElements}</span> market
                insights
              </span>

              <div className="flex items-center gap-1">
                <button
                  onClick={() => !data.first && setPage((p) => p - 1)}
                  disabled={data.first}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs transition-all"
                  style={{
                    fontFamily: fonts.mono,
                    color: data.first ? colors.textFaint : colors.textMuted,
                    background: "transparent",
                    border: `1px solid ${colors.border}`,
                    cursor: data.first ? "not-allowed" : "pointer",
                  }}
                  onMouseEnter={(e) => {
                    if (!data.first) e.currentTarget.style.color = colors.textPrimary;
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = data.first
                      ? colors.textFaint
                      : colors.textMuted;
                  }}
                >
                  <ChevronLeft size={13} />
                  Prev
                </button>

                <div className="flex items-center gap-1 mx-1">
                  {pageNumbers.map((pageNumber, i) => {
                    const isActive = pageNumber === page;
                    const prevPage = pageNumbers[i - 1];
                    const showEllipsis =
                      prevPage !== undefined && pageNumber - prevPage > 1;

                    return (
                      <span key={pageNumber} className="flex items-center gap-1">
                        {showEllipsis && (
                          <span
                            className="px-1 text-xs"
                            style={{ fontFamily: fonts.mono, color: colors.textFaint }}
                          >
                            …
                          </span>
                        )}
                        <button
                          onClick={() => setPage(pageNumber)}
                          className="w-8 h-8 rounded-lg text-xs font-semibold transition-all"
                          style={{
                            fontFamily: fonts.mono,
                            color: isActive ? colors.accent : colors.textMuted,
                            background: isActive ? withAlpha("accent", 0.08) : "transparent",
                            border: isActive
                              ? `1px solid ${withAlpha("accent", 0.2)}`
                              : "1px solid transparent",
                          }}
                          onMouseEnter={(e) => {
                            if (!isActive) e.currentTarget.style.color = colors.textPrimary;
                          }}
                          onMouseLeave={(e) => {
                            if (!isActive) e.currentTarget.style.color = colors.textMuted;
                          }}
                        >
                          {pageNumber + 1}
                        </button>
                      </span>
                    );
                  })}
                </div>

                <button
                  onClick={() => !data.last && setPage((p) => p + 1)}
                  disabled={data.last}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs transition-all"
                  style={{
                    fontFamily: fonts.mono,
                    color: data.last ? colors.textFaint : colors.textMuted,
                    background: "transparent",
                    border: `1px solid ${colors.border}`,
                    cursor: data.last ? "not-allowed" : "pointer",
                  }}
                  onMouseEnter={(e) => {
                    if (!data.last) e.currentTarget.style.color = colors.textPrimary;
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = data.last
                      ? colors.textFaint
                      : colors.textMuted;
                  }}
                >
                  Next
                  <ChevronRight size={13} />
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
