"use client";

import { ColumnDef } from "@tanstack/react-table";
import { InvestmentInsightSummary } from "../types/investment-insight.types";
import { TrendingUp, TrendingDown, Minus } from "lucide-react";

// ── Risk badge ────────────────────────────────────────────────────────────────
function RiskBadge({ risk }: { risk: string }) {
  const styles: Record<string, { color: string; bg: string; border: string }> = {
    HIGH:   { color: "#FF4560", bg: "#FF456018", border: "#FF456044" },
    MEDIUM: { color: "#FFB800", bg: "#FFB80018", border: "#FFB80044" },
    LOW:    { color: "#00FF94", bg: "#00FF9418", border: "#00FF9444" },
  };

  const s = styles[risk?.toUpperCase()] ?? styles.MEDIUM;

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
        fontFamily: "'JetBrains Mono', monospace",
        color: s.color,
        background: s.bg,
        border: `1px solid ${s.border}`,
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

  const color = isAccumulate ? "#00FF94" : isReduce ? "#FF4560" : "#FFB800";
  const bg    = isAccumulate ? "#00FF9415" : isReduce ? "#FF456015" : "#FFB80015";
  const Icon  = isAccumulate ? TrendingUp : isReduce ? TrendingDown : Minus;

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
        fontFamily: "'JetBrains Mono', monospace",
        color,
        background: bg,
        border: `1px solid ${color}44`,
      }}
    >
      <Icon size={11} />
      {action}
    </span>
  );
}

// ── Score cell ────────────────────────────────────────────────────────────────
function ScoreCell({ score }: { score: number }) {
  const color =
    score >= 70 ? "#00FF94" : score >= 40 ? "#FFB800" : "#FF4560";

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
      {/* Mini bar */}
      <div
        style={{
          width: "64px",
          height: "4px",
          borderRadius: "2px",
          background: "#1a2744",
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
            boxShadow: `0 0 6px ${color}88`,
          }}
        />
      </div>
      <span
        style={{
          fontFamily: "'JetBrains Mono', monospace",
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

}: { symbol: string; 
    companyName: string 

}) {
  return (
    <div className="flex flex-col">
    <span
        style={{
            fontFamily: "'JetBrains Mono', monospace",
            fontSize: "13px",
            fontWeight: 700,
            color: "#00D4FF",
            letterSpacing: "0.04em",
        }}
        >
        {symbol}
        </span>

        <span        
        style={{
            fontFamily: "'JetBrains Mono', monospace",
            fontSize: "12px",
            color: "#6B7FA3",
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
        fontFamily: "'JetBrains Mono', monospace",
        fontSize: "12px",
        color: "#6B7FA3",
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
    cell: ({ row }) => <SymbolCell 
                            symbol={row.original.symbol} 
                            companyName={row.original.companyName}
                         />,
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