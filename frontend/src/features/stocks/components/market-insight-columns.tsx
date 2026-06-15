"use client";

import { ColumnDef } from "@tanstack/react-table";
import { MarketInsightHistoryResponse } from "../types/stock-overview";

// ── Summary cell ──────────────────────────────────────────────────────────────
function SummaryCell({ summary }: { summary: string }) {
  return (
    <span
      title={summary}
      style={{
        color: "#A8BBDB",
        fontSize: "13px",
        display: "block",
        maxWidth: "420px",
        overflow: "hidden",
        textOverflow: "ellipsis",
        whiteSpace: "nowrap",
      }}
    >
      {summary}
    </span>
  );
}

// ── Sentiment badge ───────────────────────────────────────────────────────────
function SentimentBadge({ sentiment }: { sentiment: string }) {
  const styles: Record<string, { color: string; bg: string; border: string }> = {
    BULLISH: { color: "#00FF94", bg: "#00FF9418", border: "#00FF9444" },
    BEARISH: { color: "#FF4560", bg: "#FF456018", border: "#FF456044" },
    NEUTRAL: { color: "#FFB800", bg: "#FFB80018", border: "#FFB80044" },
  };

  const s = styles[sentiment?.toUpperCase()] ?? styles.NEUTRAL;

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
      {sentiment}
    </span>
  );
}

// ── Importance cell ───────────────────────────────────────────────────────────
function ImportanceCell({ score }: { score: number }) {
  const color = score >= 80 ? "#00FF94" : score >= 50 ? "#FFB800" : "#FF4560";

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
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
          color,
          fontWeight: 600,
          fontSize: "13px",
          fontFamily: "'JetBrains Mono', monospace",
        }}
      >
        {score}
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
export const marketInsightColumns: ColumnDef<MarketInsightHistoryResponse>[] = [
  {
    accessorKey: "summary",
    header: "Summary",
    cell: ({ row }) => <SummaryCell summary={row.original.summary} />,
  },
  {
    accessorKey: "sentiment",
    header: "Sentiment",
    cell: ({ row }) => <SentimentBadge sentiment={row.original.sentiment} />,
  },
  {
    accessorKey: "importanceScore",
    header: "Importance",
    cell: ({ row }) => <ImportanceCell score={row.original.importanceScore} />,
  },
  {
    accessorKey: "generatedAt",
    header: "Generated",
    cell: ({ row }) => <DateCell dateStr={row.original.generatedAt} />,
  },
];