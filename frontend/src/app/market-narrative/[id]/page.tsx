"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import {
  ChevronLeft,
  ExternalLink,
  Newspaper,
  FileText,
  Calendar,
  Activity,
  AlertCircle,
} from "lucide-react";

import { useNarrativeDetails } from "@/features/market-narrative/hooks/useNarrativeSourceDetails";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

const toTime = (d?: string | null) => {
  if (!d) return Number.NEGATIVE_INFINITY;
  const t = new Date(d).getTime();
  return Number.isNaN(t) ? Number.NEGATIVE_INFINITY : t;
};

export default function MarketNarrativePage() {
  const params = useParams();
  const id = params.id as string;

  const { data, isLoading, error } = useNarrativeDetails(id);

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
            Loading narrative sources…
          </span>
        </div>
      </div>
    );
  }

  if (error || !data?.marketNarrativeSource?.length) {
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
            Failed to load narrative sources.
          </span>
        </div>
      </div>
    );
  }

  const sources = [...data.marketNarrativeSource].sort(
    (a, b) => toTime(b.publishedDate) - toTime(a.publishedDate)
  );
  const firstSource = sources[0];

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
            href={`/stocks/${firstSource.symbol}`}
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: colors.textMuted }}
            onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
            onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
          >
            <ChevronLeft size={14} />
            Back to Stock
          </Link>

          <div
            className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-lg"
            style={{
              fontFamily: fonts.mono,
              color: colors.textMuted,
              background: colors.bgSurface,
              border: `1px solid ${colors.border}`,
            }}
          >
            <Newspaper size={11} style={{ color: colors.accent }} />
            {sources.length} sources
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
              <FileText size={22} style={{ color: colors.accent }} />
            </div>

            <div className="flex-1 min-w-0">
              <h1
                className="text-4xl font-bold leading-none"
                style={{ color: colors.accent, fontFamily: fonts.mono }}
              >
                {firstSource.symbol}
              </h1>
              <p className="mt-2 text-lg" style={{ color: colors.textMuted }}>
                {firstSource.companyName}
              </p>
              <p className="mt-4 text-sm leading-6" style={{ color: colors.textMuted }}>
                Articles used to build the current market narrative intelligence.
              </p>
            </div>
          </div>
        </div>

        {/* ── Section eyebrow ── */}
        <div className="flex items-center gap-2.5">
          <div
            className="w-7 h-7 rounded-lg flex items-center justify-center shrink-0"
            style={{
              background: withAlpha("accent", 0.07),
              border: `1px solid ${withAlpha("accent", 0.15)}`,
            }}
          >
            <Newspaper size={13} style={{ color: colors.accent }} />
          </div>
          <h2
            className="text-xs font-semibold uppercase"
            style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
          >
            Supporting Sources
          </h2>
          <span
            className="text-xs"
            style={{ fontFamily: fonts.mono, color: colors.textFaint }}
          >
            · {sources.length}
          </span>
        </div>

        {/* ── Sources list ── */}
        <div className="space-y-3">
          {sources.map((source, index) => (
            <a
              key={`${source.sourceUrl}-${index}`}
              href={source.sourceUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="group block rounded-xl px-6 py-5 transition-colors"
              style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = withAlpha("accent", 0.2);
                e.currentTarget.style.background = colors.bgHover;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = colors.border;
                e.currentTarget.style.background = colors.bgSurface;
              }}
            >
              <div className="flex items-start gap-4">
                {/* Index badge */}
                <div
                  className="w-8 h-8 rounded-lg flex items-center justify-center shrink-0 text-xs font-semibold"
                  style={{
                    fontFamily: fonts.mono,
                    color: colors.accent,
                    background: withAlpha("accent", 0.07),
                    border: `1px solid ${withAlpha("accent", 0.15)}`,
                  }}
                >
                  {index + 1}
                </div>

                <div className="flex-1 min-w-0">
                  <h3
                    className="text-base font-medium leading-snug transition-colors"
                    style={{ color: colors.textPrimary }}
                  >
                    {source.title}
                  </h3>

                  <div
                    className="mt-2 flex items-center gap-1.5 text-xs"
                    style={{ fontFamily: fonts.mono, color: colors.textMuted }}
                  >
                    <Calendar size={12} />
                    {source.publishedDate
                      ? new Date(source.publishedDate).toLocaleDateString()
                      : "Date unknown"}
                  </div>
                </div>

                {/* External affordance — brightens on card hover */}
                <ExternalLink
                  size={16}
                  className="shrink-0 mt-1 transition-colors"
                  style={{ color: colors.textFaint }}
                />
              </div>
            </a>
          ))}
        </div>
      </div>
    </div>
  );
}
