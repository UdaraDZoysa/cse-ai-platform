"use client";

import Link from "next/link";
import { LineChart, Sparkles, ArrowRight } from "lucide-react";

import WatchlistCard from "@/features/dashboard/components/WatchlistCard";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

export default function DashboardPage() {
  return (
    <div className="min-h-screen" style={{ background: colors.bgPage }}>
      <div
        className="sticky top-0 z-10 px-8 py-4"
        style={{
          background: withAlpha("page", 0.93),
          backdropFilter: "blur(12px)",
          borderBottom: `1px solid ${colors.border}`,
        }}
      >
        <div className="max-w-4xl mx-auto flex items-center">
          <div className="flex items-center gap-2.5">
            <div
              className="w-8 h-8 rounded-lg flex items-center justify-center"
              style={{
                background: withAlpha("accent", 0.1),
                border: `1px solid ${withAlpha("accent", 0.2)}`,
              }}
            >
              <LineChart size={16} style={{ color: colors.accent }} />
            </div>
            <span
              className="text-sm font-semibold"
              style={{ fontFamily: fonts.mono, color: colors.textPrimary }}
            >
              CSE Intelligence
            </span>
          </div>
        </div>
      </div>

      <div className="px-8 py-8 max-w-4xl mx-auto space-y-6">
        <div
          className="rounded-2xl px-7 py-6"
          style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
        >
          <p
            className="text-xs font-semibold uppercase"
            style={{ fontFamily: fonts.mono, color: colors.textEyebrow, letterSpacing: "0.18em" }}
          >
            Dashboard
          </p>
          <h1 className="mt-2 text-3xl font-bold" style={{ color: colors.textPrimary }}>
            Colombo Stock Exchange Intelligence
          </h1>
          <p className="mt-2 text-sm" style={{ color: colors.textMuted }}>
            Track the stocks you care about and jump straight into AI-generated
            investment insights.
          </p>

          <Link
            href="/investment-insights"
            className="mt-5 inline-flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-semibold transition-colors"
            style={{
              fontFamily: fonts.mono,
              color: colors.accent,
              background: withAlpha("accent", 0.12),
              border: `1px solid ${withAlpha("accent", 0.2)}`,
            }}
            onMouseEnter={(e) => (e.currentTarget.style.background = withAlpha("accent", 0.2))}
            onMouseLeave={(e) => (e.currentTarget.style.background = withAlpha("accent", 0.12))}
          >
            <Sparkles size={15} />
            Browse all Investment Insights
            <ArrowRight size={15} />
          </Link>
        </div>

        {/* ── Watchlist ── */}
        <WatchlistCard />
      </div>
    </div>
  );
}