"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useInvestmentInsightDetails } from "@/features/investment-insights/hooks/useInvestmentInsightDetails";
import {
  ArrowLeft,
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
} from "lucide-react";

// ── Helpers ──────────────────────────────────────────────────────────────────

function getActionStyle(action: string) {
  const a = action?.toUpperCase();
  if (a === "ACCUMULATE" || a === "BUY")
    return { color: "#00FF94", bg: "#00FF9415", border: "#00FF9433" };
  if (a === "REDUCE" || a === "SELL" || a === "AVOID")
    return { color: "#FF4560", bg: "#FF456015", border: "#FF456033" };
  return { color: "#FFB800", bg: "#FFB80015", border: "#FFB80033" };
}

function getRiskStyle(risk: string) {
  const r = risk?.toUpperCase();
  if (r === "HIGH")
    return { color: "#FF4560", bg: "#FF456015", border: "#FF456033", label: "High Risk" };
  if (r === "LOW")
    return { color: "#00FF94", bg: "#00FF9415", border: "#00FF9433", label: "Low Risk" };
  return { color: "#FFB800", bg: "#FFB80015", border: "#FFB80033", label: "Medium Risk" };
}

function getScoreColor(score: number) {
  if (score >= 75) return "#00FF94";
  if (score >= 50) return "#FFB800";
  return "#FF4560";
}

function ScoreArc({ score }: { score: number }) {
  const color = getScoreColor(score);
  const radius = 52;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (score / 100) * circumference;

  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width="128" height="128" className="-rotate-90">
        <circle
          cx="64" cy="64" r={radius}
          fill="none"
          stroke="#1a2744"
          strokeWidth="8"
        />
        <circle
          cx="64" cy="64" r={radius}
          fill="none"
          stroke={color}
          strokeWidth="8"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          style={{
            filter: `drop-shadow(0 0 6px ${color}88)`,
            transition: "stroke-dashoffset 1s ease",
          }}
        />
      </svg>
      <div className="absolute flex flex-col items-center">
        <span
          className="text-3xl font-bold leading-none"
          style={{ fontFamily: "'JetBrains Mono', monospace", color }}
        >
          {score}
        </span>
        <span className="text-xs mt-1" style={{ color: "#6B7FA3" }}>
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
  accentColor = "#00D4FF",
}: {
  icon: React.ReactNode;
  label: string;
  children: React.ReactNode;
  accentColor?: string;
}) {
  return (
    <div
      className="rounded-xl p-5"
      style={{
        background: "#0F1629",
        border: "1px solid #1a2744",
      }}
    >
      <div className="flex items-center gap-2 mb-4">
        <div
          className="w-6 h-6 rounded-md flex items-center justify-center flex-shrink-0"
          style={{ background: `${accentColor}18`, color: accentColor }}
        >
          {icon}
        </div>
        <h2
          className="text-xs font-semibold uppercase tracking-widest"
          style={{ color: "#6B7FA3" }}
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
        style={{ background: "#0A0E1A" }}
      >
        <div
          className="text-center p-8 rounded-xl"
          style={{ background: "#0F1629", border: "1px solid #FF456033" }}
        >
          <XCircle size={32} style={{ color: "#FF4560" }} className="mx-auto mb-3" />
          <p className="text-sm" style={{ color: "#6B7FA3" }}>
            Failed to load insight.
          </p>
        </div>
      </div>
    );
  }

  const actionStyle = getActionStyle(data.action);
  const riskStyle = getRiskStyle(data.riskLevel);
  const scoreColor = getScoreColor(data.opportunityScore);

  return (
    <div className="min-h-screen" style={{ background: "#0A0E1A" }}>
      {/* Top bar */}
      <div
        className="sticky top-0 z-10 px-8 py-4 flex items-center justify-between"
        style={{
          background: "#0A0E1Aee",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid #1a2744",
        }}
      >
        <Link
          href="/investment-insights"
          className="flex items-center gap-2 text-sm transition-colors"
          style={{ color: "#6B7FA3" }}
          onMouseEnter={(e) =>
            (e.currentTarget.style.color = "#E8EEF8")
          }
          onMouseLeave={(e) =>
            (e.currentTarget.style.color = "#6B7FA3")
          }
        >
          <ArrowLeft size={14} />
          Back to Insights
        </Link>

        <div
          className="flex items-center gap-2 text-xs"
          style={{
            fontFamily: "'JetBrains Mono', monospace",
            color: "#3D5080",
          }}
        >
          <Zap size={11} style={{ color: "#00D4FF" }} />
          Generated {new Date(data.createdAt).toLocaleString()}
        </div>
      </div>

      <div className="px-8 py-8 max-w-6xl mx-auto space-y-6">
        {/* ── Hero ── */}
        <div
          className="rounded-2xl p-7"
          style={{
            background: "linear-gradient(135deg, #0F1629, #141e35)",
            border: "1px solid #1a2744",
          }}
        >
          <div className="flex items-start justify-between gap-6">
            {/* Symbol + action */}
            <div>
              <div className="flex items-center gap-4 flex-wrap">
                <div>   
                    <h1
                    className="text-5xl font-bold leading-none"
                    style={{
                        fontFamily: "'JetBrains Mono', monospace",
                        color: "#00D4FF",
                    }}
                    >
                    {data.symbol}
                    </h1>
                    <p
                        className="mt-2 text-1xl"
                        style={{
                            color: "#A8BBDB",
                        }}
                        >
                        {data.companyName}
                    </p>
                </div>
                <span
                  className="px-4 py-1.5 rounded-lg text-sm font-bold tracking-wider"
                  style={{
                    fontFamily: "'JetBrains Mono', monospace",
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
                    fontFamily: "'JetBrains Mono', monospace",
                    color: riskStyle.color,
                    background: riskStyle.bg,
                    border: `1px solid ${riskStyle.border}`,
                  }}
                >
                  {riskStyle.label}
                </span>
              </div>

              <p
                className="text-sm mt-3"
                style={{ color: "#6B7FA3" }}
              >
                Investment Intelligence Report
              </p>
            </div>

            {/* Score arc */}
            <div className="flex flex-col items-center gap-1 flex-shrink-0">
              <ScoreArc score={data.opportunityScore} />
              <span
                className="text-xs uppercase tracking-widest"
                style={{ color: "#3D5080" }}
              >
                Opportunity Score
              </span>
            </div>
          </div>

          {/* Score bar (linear, below hero) */}
          <div className="mt-6">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs" style={{ color: "#3D5080" }}>
                Score confidence
              </span>
              <span
                className="text-xs font-semibold"
                style={{
                  fontFamily: "'JetBrains Mono', monospace",
                  color: scoreColor,
                }}
              >
                {data.opportunityScore} / 100
              </span>
            </div>
            <div
              className="w-full h-2 rounded-full overflow-hidden"
              style={{ background: "#1a2744" }}
            >
              <div
                className="h-full rounded-full transition-all duration-1000"
                style={{
                  width: `${data.opportunityScore}%`,
                  background: `linear-gradient(90deg, ${scoreColor}88, ${scoreColor})`,
                  boxShadow: `0 0 10px ${scoreColor}66`,
                }}
              />
            </div>
          </div>
        </div>

        {/* ── Executive Summary ── */}
        <InfoCard
          icon={<Brain size={14} />}
          label="Executive Summary"
          accentColor="#00D4FF"
        >
          <p
            className="text-sm leading-relaxed"
            style={{ color: "#A8BBDB" }}
          >
            {data.executiveSummary}
          </p>
        </InfoCard>

        {/* ── Three reasoning cards ── */}
        <div className="grid md:grid-cols-3 gap-4">
          <InfoCard
            icon={<BarChart3 size={14} />}
            label="Market Assessment"
            accentColor="#4D9EFF"
          >
            <p className="text-sm leading-relaxed" style={{ color: "#A8BBDB" }}>
              {data.marketReasoning}
            </p>
          </InfoCard>

          <InfoCard
            icon={<Activity size={14} />}
            label="Confidence Assessment"
            accentColor="#00D4FF"
          >
            <p className="text-sm leading-relaxed" style={{ color: "#A8BBDB" }}>
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
          >
            <p className="text-sm leading-relaxed" style={{ color: "#A8BBDB" }}>
              {data.actionReasoning}
            </p>
          </InfoCard>
        </div>

        {/* ── Factors + Risks ── */}
        <div className="grid md:grid-cols-2 gap-4">
          {/* Supporting Factors */}
          <div
            className="rounded-xl p-5"
            style={{ background: "#0F1629", border: "1px solid #1a2744" }}
          >
            <div className="flex items-center gap-2 mb-4">
              <div
                className="w-6 h-6 rounded-md flex items-center justify-center"
                style={{ background: "#00FF9418", color: "#00FF94" }}
              >
                <CheckCircle2 size={14} />
              </div>
              <h2
                className="text-xs font-semibold uppercase tracking-widest"
                style={{ color: "#6B7FA3" }}
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
                    background: "#141e35",
                    border: "1px solid #1a2744",
                    color: "#A8BBDB",
                  }}
                >
                  <span
                    className="mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0"
                    style={{ background: "#00FF94" }}
                  />
                  {factor}
                </li>
              ))}
            </ul>
          </div>

          {/* Risks */}
          <div
            className="rounded-xl p-5"
            style={{ background: "#0F1629", border: "1px solid #1a2744" }}
          >
            <div className="flex items-center gap-2 mb-4">
              <div
                className="w-6 h-6 rounded-md flex items-center justify-center"
                style={{ background: "#FF456018", color: "#FF4560" }}
              >
                <ShieldAlert size={14} />
              </div>
              <h2
                className="text-xs font-semibold uppercase tracking-widest"
                style={{ color: "#6B7FA3" }}
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
                    background: "#141e35",
                    border: "1px solid #1a2744",
                    color: "#A8BBDB",
                  }}
                >
                  <span
                    className="mt-1.5 w-1.5 h-1.5 rounded-full flex-shrink-0"
                    style={{ background: "#FF4560" }}
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