"use client";

import { ColumnDef } from "@tanstack/react-table";
import { InvestmentInsightSummary } from "../types/investment-insight.types";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";
import { colors, fonts, statusColor, withAlpha, AlphaKey } from "@/theme/theme";

// ── Risk badge ────────────────────────────────────────────────────────────────
function RiskBadge({ risk }: { risk: string }) {
  const keyByRisk: Record<string, AlphaKey> = {
    HIGH: "negative",
    MEDIUM: "warning",
    LOW: "positive",
  };
  const key = keyByRisk[risk?.toUpperCase()] ?? "warning";

  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        padding: "3px 10px",
        borderRadius: "6px",
        fontSize: "11px",
        fontWeight: 600,
        letterSpacing: "0.06em",
        fontFamily: fonts.mono,
        color: statusColor[key as "negative" | "warning" | "positive"],
        background: withAlpha(key, 0.1),
        border: `1px solid ${withAlpha(key, 0.27)}`,
      }}
    >
      {risk}
    </span>
  );
}

// ── Action badge ──────────────────────────────────────────────────────────────
function ActionBadge({ action }: { action: string }) {
  const a = action?.toUpperCase();
  const isAccumulate = a === "ACCUMULATE" || a === "BUY";
  const isReduce = a === "REDUCE" || a === "SELL" || a === "AVOID";

  const key: AlphaKey = isAccumulate ? "positive" : isReduce ? "negative" : "warning";
  const color = statusColor[key as "positive" | "negative" | "warning"];
  const Icon = isAccumulate ? TrendingUp : isReduce ? TrendingDown : Minus;

  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: "5px",
        padding: "3px 10px",
        borderRadius: "6px",
        fontSize: "11px",
        fontWeight: 700,
        letterSpacing: "0.06em",
        fontFamily: fonts.mono,
        color,
        background: withAlpha(key, 0.08),
        border: `1px solid ${withAlpha(key, 0.27)}`,
      }}
    >
      <Icon size={11} />
      {action}
    </span>
  );
}

// ── Score cell ────────────────────────────────────────────────────────────────
function ScoreCell({ score }: { score: number }) {
  const key: AlphaKey = score >= 70 ? "positive" : score >= 40 ? "warning" : "negative";
  const color = statusColor[key as "positive" | "warning" | "negative"];

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
      {/* Mini bar */}
      <div
        style={{
          width: "64px",
          height: "4px",
          borderRadius: "2px",
          background: colors.border,
          overflow: "hidden",
          flexShrink: 0,
        }}
      >
        <div
          style={{
            width: `${score}%`,
            height: "100%",
            borderRadius: "2px",
            background: color,
            boxShadow: `0 0 6px ${withAlpha(key, 0.53)}`,
          }}
        />
      </div>
      <span
        style={{
          fontFamily: fonts.mono,
          fontSize: "13px",
          fontWeight: 600,
          color,
        }}
      >
        {score}
      </span>
    </div>
  );
}

// ── Symbol cell ───────────────────────────────────────────────────────────────
function SymbolCell({
  symbol,
  companyName,
}: {
  symbol: string;
  companyName: string;
}) {
  return (
    <div className="flex flex-col">
      <span
        style={{
          fontFamily: fonts.mono,
          fontSize: "13px",
          fontWeight: 700,
          color: colors.accent,
          letterSpacing: "0.04em",
        }}
      >
        {symbol}
      </span>

      <span
        style={{
          fontFamily: fonts.mono,
          fontSize: "12px",
          color: colors.textMuted,
          marginTop: "2px",
        }}
      >
        {companyName}
      </span>
    </div>
  );
}

// ── Date cell ─────────────────────────────────────────────────────────────────
function DateCell({ dateStr }: { dateStr: string }) {
  return (
    <span
      style={{
        fontFamily: fonts.mono,
        fontSize: "12px",
        color: colors.textMuted,
      }}
    >
      {new Date(dateStr).toLocaleString()}
    </span>
  );
}

// ── Column definitions ────────────────────────────────────────────────────────
export const columns: ColumnDef<InvestmentInsightSummary>[] = [
  {
    accessorKey: "symbol",
    header: "Symbol",
    cell: ({ row }) => (
      <SymbolCell symbol={row.original.symbol} companyName={row.original.companyName} />
    ),
  },
  {
    accessorKey: "action",
    header: "Action",
    cell: ({ row }) => <ActionBadge action={row.original.action} />,
  },
  {
    accessorKey: "opportunityScore",
    header: "Opportunity Score",
    cell: ({ row }) => <ScoreCell score={row.original.opportunityScore} />,
  },
  {
    accessorKey: "riskLevel",
    header: "Risk Level",
    cell: ({ row }) => <RiskBadge risk={row.original.riskLevel} />,
  },
  {
    accessorKey: "createdAt",
    header: "Created At",
    cell: ({ row }) => <DateCell dateStr={row.original.createdAt} />,
  },
];
