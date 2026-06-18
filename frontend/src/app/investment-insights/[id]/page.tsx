"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useInvestmentInsightDetails } from "@/features/investment-insights/hooks/useInvestmentInsightDetails";
import {
  TrendingUp,
  TrendingDown,
  Minus,
  ShieldAlert,
  CheckCircle2,
  XCircle,
  Brain,
  BarChart3,
  Zap,
  Activity,
  ChevronLeft,
} from "lucide-react";

import { colors, fonts, gradients, statusColor, withAlpha, AlphaKey } from "@/theme/theme";
import { useThemeColors } from "@/theme/useThemeColors";

// ── Helpers ──────────────────────────────────────────────────────────────────

type StatusKey = "positive" | "negative" | "warning";

interface Swatch {
  key: StatusKey;
  color: string;
  bg: string;
  border: string;
}

function swatch(key: StatusKey): Swatch {
  return {
    key,
    color: statusColor[key],
    bg: withAlpha(key, 0.08),
    border: withAlpha(key, 0.2),
  };
}

function getActionStyle(action: string): Swatch {
  const a = action?.toUpperCase();
  if (a === "ACCUMULATE" || a === "BUY") return swatch("positive");
  if (a === "REDUCE" || a === "SELL" || a === "AVOID") return swatch("negative");
  return swatch("warning");
}

function getRiskStyle(risk: string): Swatch & { label: string } {
  const r = risk?.toUpperCase();
  if (r === "HIGH") return { ...swatch("negative"), label: "High Risk" };
  if (r === "LOW") return { ...swatch("positive"), label: "Low Risk" };
  return { ...swatch("warning"), label: "Medium Risk" };
}

function getScoreKey(score: number): StatusKey {
  if (score >= 75) return "positive";
  if (score >= 50) return "warning";
  return "negative";
}

function ScoreArc({ score }: { score: number }) {
  const c = useThemeColors();
  const key = getScoreKey(score);
  const color = c[key];
  const radius = 52;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width="128" height="128" className="-rotate-90">
        <circle cx="64" cy="64" r={radius} fill="none" stroke={c.border} strokeWidth="8" />
        <circle
          cx="64"
          cy="64"
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth="8"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          style={{
            filter: `drop-shadow(0 0 6px ${withAlpha(key, 0.53)})`,
            transition: "stroke-dashoffset 1s ease",
          }}
        />
      </svg>
      <div className="absolute flex flex-col items-center">
        <span
          className="text-3xl font-bold leading-none"
          style={{ fontFamily: fonts.mono, color }}
        >
          {score}
        </span>
        <span className="text-xs mt-1" style={{ color: colors.textMuted }}>
          / 100
        </span>
      </div>
    </div>
  );
}

function InfoCard({
  icon,
  label,
  children,
  accentColor = colors.accent,
  accentKey = "accent",
}: {
  icon: React.ReactNode;
  label: string;
  children: React.ReactNode;
  accentColor?: string;
  accentKey?: AlphaKey;
}) {
  return (
    <div
      className="rounded-xl p-5"
      style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
    >
      <div className="flex items-center gap-2 mb-4">
        <div
          className="w-6 h-6 rounded-md flex items-center justify-center flex-shrink-0"
          style={{ background: withAlpha(accentKey, 0.1), color: accentColor }}
        >
          {icon}
        </div>
        <h2
          className="text-xs font-semibold uppercase"
          style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
        >
          {label}
        </h2>
      </div>
      {children}
    </div>
  );
}

// ── Page ─────────────────────────────────────────────────────────────────────

export default function InvestmentInsightDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const { data, isLoading, error } = useInvestmentInsightDetails(id);

  if (isLoading) {
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
            Loading intelligence…
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
          className="text-center p-8 rounded-xl"
          style={{ background: colors.bgSurface, border: `1px solid ${withAlpha("negative", 0.2)}` }}
        >
          <XCircle size={32} style={{ color: colors.negative }} className="mx-auto mb-3" />
          <p className="text-sm" style={{ color: colors.textMuted }}>
            Failed to load insight.
          </p>
        </div>
      </div>
    );
  }

  const actionStyle = getActionStyle(data.action);
  const riskStyle = getRiskStyle(data.riskLevel);
  const scoreKey = getScoreKey(data.opportunityScore);
  const scoreColor = statusColor[scoreKey];

  return (
    <div className="min-h-screen" style={{ background: colors.bgPage }}>
      {/* Top bar */}
      <div
        className="sticky top-0 z-10"
        style={{
          background: withAlpha("page", 0.93),
          backdropFilter: "blur(12px)",
          borderBottom: `1px solid ${colors.border}`,
        }}
      >
        <div className="max-w-6xl mx-auto px-8 py-4 flex items-center justify-between">
          <Link
            href={`/stocks/${data.symbol}/investment-insights`}
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: colors.textMuted }}
            onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
            onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
          >
            <ChevronLeft size={14} />
            Back to Insights
          </Link>

          <div
            className="flex items-center gap-2 text-xs"
            style={{ fontFamily: fonts.mono, color: colors.textFaint }}
          >
            <Zap size={11} style={{ color: colors.accent }} />
            Generated {new Date(data.createdAt).toLocaleString()}
          </div>
        </div>
      </div>

      <div className="px-8 py-8 max-w-6xl mx-auto space-y-6">
        {/* ── Hero ── */}
        <div
          className="rounded-2xl p-7"
          style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
        >
          <div className="flex items-start justify-between gap-6">
            {/* Symbol + action */}
            <div>
              <div className="flex items-center gap-4 flex-wrap">
                <div>
                  <Link href={`/stocks/${data.symbol}`} className="group inline-block">
                    <h1
                      className="text-5xl font-bold leading-none"
                      style={{ fontFamily: fonts.mono, color: colors.accent }}
                    >
                      {data.symbol}
                    </h1>
                    <p className="mt-2 text-xl" style={{ color: colors.textBody }}>
                      {data.companyName}
                    </p>
                  </Link>
                </div>
                <span
                  className="px-4 py-1.5 rounded-lg text-sm font-bold tracking-wider"
                  style={{
                    fontFamily: fonts.mono,
                    color: actionStyle.color,
                    background: actionStyle.bg,
                    border: `1px solid ${actionStyle.border}`,
                  }}
                >
                  {data.action}
                </span>

                <span
                  className="px-3 py-1.5 rounded-lg text-xs font-semibold"
                  style={{
                    fontFamily: fonts.mono,
                    color: riskStyle.color,
                    background: riskStyle.bg,
                    border: `1px solid ${riskStyle.border}`,
                  }}
                >
                  {riskStyle.label}
                </span>
              </div>

              <p className="text-sm mt-3" style={{ color: colors.textMuted }}>
                Investment Intelligence Report
              </p>
            </div>

            {/* Score arc */}
            <div className="flex flex-col items-center gap-1 flex-shrink-0">
              <ScoreArc score={data.opportunityScore} />
              <span
                className="text-xs uppercase tracking-widest"
                style={{ color: colors.textFaint }}
              >
                Opportunity Score
              </span>
            </div>
          </div>

          {/* Score bar (linear, below hero) */}
          <div className="mt-6">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs" style={{ color: colors.textFaint }}>
                Score confidence
              </span>
              <span
                className="text-xs font-semibold"
                style={{ fontFamily: fonts.mono, color: scoreColor }}
              >
                {data.opportunityScore} / 100
              </span>
            </div>
            <div
              className="w-full h-2 rounded-full overflow-hidden"
              style={{ background: colors.border }}
            >
              <div
                className="h-full rounded-full transition-all duration-1000"
                style={{
                  width: `${data.opportunityScore}%`,
                  background: `linear-gradient(90deg, ${withAlpha(scoreKey, 0.53)}, ${scoreColor})`,
                  boxShadow: `0 0 10px ${withAlpha(scoreKey, 0.4)}`,
                }}
              />
            </div>
          </div>
        </div>

        {/* ── Executive Summary ── */}
        <InfoCard icon={<Brain size={14} />} label="Executive Summary" accentColor={colors.accent} accentKey="accent">
          <p className="text-sm leading-relaxed" style={{ color: colors.textBody }}>
            {data.executiveSummary}
          </p>
        </InfoCard>

        {/* ── Three reasoning cards ── */}
        <div className="grid md:grid-cols-3 gap-4">
          <InfoCard icon={<BarChart3 size={14} />} label="Market Assessment" accentColor={colors.info} accentKey="info">
            <p className="text-sm leading-relaxed" style={{ color: colors.textBody }}>
              {data.marketReasoning}
            </p>
          </InfoCard>

          <InfoCard icon={<Activity size={14} />} label="Confidence Assessment" accentColor={colors.accent} accentKey="accent">
            <p className="text-sm leading-relaxed" style={{ color: colors.textBody }}>
              {data.confidenceReasoning}
            </p>
          </InfoCard>

          <InfoCard
            icon={
              data.action?.toUpperCase() === "REDUCE" ||
              data.action?.toUpperCase() === "AVOID" ? (
                <TrendingDown size={14} />
              ) : data.action?.toUpperCase() === "HOLD" ? (
                <Minus size={14} />
              ) : (
                <TrendingUp size={14} />
              )
            }
            label="Recommended Action"
            accentColor={actionStyle.color}
            accentKey={actionStyle.key}
          >
            <p className="text-sm leading-relaxed" style={{ color: colors.textBody }}>
              {data.actionReasoning}
            </p>
          </InfoCard>
        </div>

        {/* ── Factors + Risks ── */}
        <div className="grid md:grid-cols-2 gap-4">
          {/* Supporting Factors */}
          <div
            className="rounded-xl p-5"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2 mb-4">
              <div
                className="w-6 h-6 rounded-md flex items-center justify-center"
                style={{ background: withAlpha("positive", 0.1), color: colors.positive }}
              >
                <CheckCircle2 size={14} />
              </div>
              <h2
                className="text-xs font-semibold uppercase"
                style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
              >
                Supporting Factors
              </h2>
            </div>

            <ul className="space-y-2">
              {data.supportingFactors.map((factor, i) => (
                <li
                  key={i}
                  className="flex items-start gap-3 px-3 py-2.5 rounded-lg text-sm"
                  style={{
                    background: colors.bgSurfaceAlt,
                    border: `1px solid ${colors.border}`,
                    color: colors.textBody,
                  }}
                >
                  <span
                    className="mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0"
                    style={{ background: colors.positive }}
                  />
                  {factor}
                </li>
              ))}
            </ul>
          </div>

          {/* Risks */}
          <div
            className="rounded-xl p-5"
            style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2 mb-4">
              <div
                className="w-6 h-6 rounded-md flex items-center justify-center"
                style={{ background: withAlpha("negative", 0.1), color: colors.negative }}
              >
                <ShieldAlert size={14} />
              </div>
              <h2
                className="text-xs font-semibold uppercase"
                style={{ color: colors.textEyebrow, letterSpacing: "0.1em" }}
              >
                Risks
              </h2>
            </div>

            <ul className="space-y-2">
              {data.risks.map((risk, i) => (
                <li
                  key={i}
                  className="flex items-start gap-3 px-3 py-2.5 rounded-lg text-sm"
                  style={{
                    background: colors.bgSurfaceAlt,
                    border: `1px solid ${colors.border}`,
                    color: colors.textBody,
                  }}
                >
                  <span
                    className="mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0"
                    style={{ background: colors.negative }}
                  />
                  {risk}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
