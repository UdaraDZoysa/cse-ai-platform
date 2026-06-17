"use client";

import Link from "next/link";
import { ArrowRight, LineChart, Sparkles } from "lucide-react";

import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

export default function Home() {
  return (
    <div
      className="min-h-screen flex items-center justify-center px-8"
      style={{ background: colors.bgPage }}
    >
      <div
        className="w-full max-w-2xl rounded-2xl px-10 py-12 text-center"
        style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
      >
        <div
          className="mx-auto mb-6 flex h-14 w-14 items-center justify-center rounded-xl"
          style={{
            background: withAlpha("accent", 0.1),
            border: `1px solid ${withAlpha("accent", 0.2)}`,
          }}
        >
          <LineChart size={26} style={{ color: colors.accent }} />
        </div>

        <p
          className="mb-3 text-xs font-semibold uppercase"
          style={{
            fontFamily: fonts.mono,
            color: colors.textEyebrow,
            letterSpacing: "0.18em",
          }}
        >
          Colombo Stock Exchange
        </p>

        <h1
          className="text-4xl font-bold leading-tight"
          style={{ color: colors.textPrimary }}
        >
          CSE Investment Intelligence
        </h1>

        <p
          className="mx-auto mt-4 max-w-md text-sm leading-6"
          style={{ color: colors.textMuted }}
        >
          AI-generated investment reviews, market sentiment, and the narratives
          moving the market — all in one place.
        </p>

        <Link
          href="/investment-insights"
          className="mt-8 inline-flex items-center gap-2 rounded-lg px-5 py-2.5 text-sm font-semibold transition-colors"
          style={{
            fontFamily: fonts.mono,
            color: colors.accent,
            background: withAlpha("accent", 0.12),
            border: `1px solid ${withAlpha("accent", 0.25)}`,
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.background = withAlpha("accent", 0.2);
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.background = withAlpha("accent", 0.12);
          }}
        >
          <Sparkles size={15} />
          View Investment Insights
          <ArrowRight size={15} />
        </Link>
      </div>
    </div>
  );
}
