"use client";

import { useParams } from "next/navigation";
import Link from "next/link";
import {
  ChevronLeft,
  Activity,
  AlertCircle,
  BarChart3,
  FileText,
  Lightbulb,
  Gauge,
  Repeat,
  Cpu,
} from "lucide-react";

import { useMarketInsightDetails } from "@/features/market-insights/hooks/useMarketInsightDetails";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

export default function MarketInsightDetailPage() {
  const params = useParams();
  const id = params.id as string;

  const { data, isLoading, error } = useMarketInsightDetails(id);

  if (isLoading) {
    return (
      <div
        className="min-h-screen flex items-center justify-center"
        style={{ background: colors.bgPage }}
      >
        <div
          className="rounded-xl py-14 px-20 flex flex-col items-center gap-3"
          style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
        >
          <Activity size={24} className="animate-pulse" style={{ color: colors.accent }} />
          <span
            className="text-sm"
            style={{ fontFamily: fonts.mono, color: colors.textMuted }}
          >
            Loading market insight…
          </span>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div
        className="min-h-screen flex items-center justify-center"
        style={{ background: colors.bgPage }}
      >
        <div
          className="rounded-xl py-14 px-20 flex flex-col items-center gap-3"
          style={{ background: colors.bgSurface, border: `1px solid ${withAlpha("negative", 0.2)}` }}
        >
          <AlertCircle size={24} style={{ color: colors.negative }} />
          <span className="text-sm" style={{ color: colors.textMuted }}>
            Failed to load market insight.
          </span>
        </div>
      </div>
    );
  }

  const isBullish = data.sentiment === "BULLISH";
  const sentimentColor = isBullish ? colors.positive : colors.negative;
  const sentimentBg = isBullish
    ? withAlpha("positive", 0.12)
    : withAlpha("negative", 0.12);
  const sentimentBorder = isBullish
    ? withAlpha("positive", 0.2)
    : withAlpha("negative", 0.2);

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
        <div className="max-w-5xl mx-auto flex items-center justify-between">
          <Link
            href={`/stocks/${data.symbol}`}
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: colors.textMuted }}
            onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
            onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
          >
            <ChevronLeft size={14} />
            Back to Stock
          </Link>

          <div
            className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-lg font-semibold"
            style={{
              fontFamily: fonts.mono,
              color: sentimentColor,
              background: sentimentBg,
              border: `1px solid ${sentimentBorder}`,
            }}
          >
            <BarChart3 size={11} />
            {data.sentiment}
          </div>
        </div>
      </div>

      <div className="px-8 py-8 max-w-5xl mx-auto space-y-6">
        {/* ── Hero ── */}
        <div
          className="rounded-2xl px-7 py-7"
          style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
        >
          <div className="flex items-start gap-4">
            <div
              className="w-12 h-12 rounded-xl flex items-center justify-center shrink-0"
              style={{
                background: withAlpha("accent", 0.1),
                border: `1px solid ${withAlpha("accent", 0.2)}`,
              }}
            >
              <BarChart3 size={22} style={{ color: colors.accent }} />
            </div>

            <div className="flex-1 min-w-0">
              <h1
                className="text-4xl font-bold leading-none"
                style={{ color: colors.accent, fontFamily: fonts.mono }}
              >
                {data.symbol}
              </h1>
              <p className="mt-2 text-lg" style={{ color: colors.textMuted }}>
                {data.companyName}
              </p>

              <div className="mt-4 flex flex-wrap gap-3">
                <span
                  className="px-3 py-1 rounded-md text-sm font-semibold"
                  style={{ background: sentimentBg, color: sentimentColor }}
                >
                  {data.sentiment}
                </span>

                <span
                  className="px-3 py-1 rounded-md text-sm font-semibold"
                  style={{ background: withAlpha("accent", 0.12), color: colors.accent }}
                >
                  Importance {data.importanceScore}
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* ── Summary ── */}
        <div
          className="rounded-xl px-7 py-6"
          style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
        >
          <div className="flex items-center gap-2.5 mb-4">
            <div
              className="w-7 h-7 rounded-lg flex items-center justify-center shrink-0"
              style={{
                background: withAlpha("accent", 0.07),
                border: `1px solid ${withAlpha("accent", 0.15)}`,
              }}
            >
              <FileText size={13} style={{ color: colors.accent }} />
            </div>
            <h2
              className="text-xs font-semibold uppercase"
              style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
            >
              Summary
            </h2>
          </div>
          <p className="leading-7" style={{ color: colors.textBody }}>
            {data.summary}
          </p>
        </div>

        {/* ── Reasoning ── */}
        <div
          className="rounded-xl px-7 py-6"
          style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
        >
          <div className="flex items-center gap-2.5 mb-4">
            <div
              className="w-7 h-7 rounded-lg flex items-center justify-center shrink-0"
              style={{
                background: withAlpha("accent", 0.07),
                border: `1px solid ${withAlpha("accent", 0.15)}`,
              }}
            >
              <Lightbulb size={13} style={{ color: colors.accent }} />
            </div>
            <h2
              className="text-xs font-semibold uppercase"
              style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
            >
              Reasoning
            </h2>
          </div>
          <p className="leading-7" style={{ color: colors.textBody }}>
            {data.reasoning}
          </p>
        </div>

        {/* ── Metrics ── */}
        <div className="grid md:grid-cols-3 gap-4">
          <div
            className="rounded-xl px-6 py-5"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2">
              <Gauge size={13} style={{ color: colors.textMuted }} />
              <h3
                className="text-[11px] font-semibold uppercase"
                style={{ color: colors.textMuted, letterSpacing: "0.08em" }}
              >
                Confidence
              </h3>
            </div>
            <p
              className="text-3xl font-bold mt-2"
              style={{ color: colors.textPrimary, fontFamily: fonts.mono }}
            >
              {data.confidenceScore}
            </p>
          </div>

          <div
            className="rounded-xl px-6 py-5"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2">
              <Repeat size={13} style={{ color: colors.textMuted }} />
              <h3
                className="text-[11px] font-semibold uppercase"
                style={{ color: colors.textMuted, letterSpacing: "0.08em" }}
              >
                Persistence
              </h3>
            </div>
            <p
              className="text-3xl font-bold mt-2"
              style={{ color: colors.textPrimary, fontFamily: fonts.mono }}
            >
              {data.persistenceScore}
            </p>
          </div>

          <div
            className="rounded-xl px-6 py-5"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2">
              <Cpu size={13} style={{ color: colors.textMuted }} />
              <h3
                className="text-[11px] font-semibold uppercase"
                style={{ color: colors.textMuted, letterSpacing: "0.08em" }}
              >
                Generated By
              </h3>
            </div>
            <p className="text-base font-semibold mt-2" style={{ color: colors.textPrimary }}>
              {data.generatedBy}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
