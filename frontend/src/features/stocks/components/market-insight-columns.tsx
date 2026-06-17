"use client";

import { ColumnDef } from "@tanstack/react-table";
import { MarketInsightHistoryResponse } from "../types/stock-overview";
import { colors, fonts, statusColor, withAlpha, AlphaKey } from "@/theme/theme";

// ── Summary cell ──────────────────────────────────────────────────────────────
function SummaryCell({ summary }: { summary: string }) {
  return (
    <span
      title={summary}
      style={{
        color: colors.textBody,
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
  const keyBySentiment: Record<string, AlphaKey> = {
    BULLISH: "positive",
    BEARISH: "negative",
    NEUTRAL: "warning",
  };
  const key = keyBySentiment[sentiment?.toUpperCase()] ?? "warning";

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
        color: statusColor[key as "positive" | "negative" | "warning"],
        background: withAlpha(key, 0.1),
        border: `1px solid ${withAlpha(key, 0.27)}`,
      }}
    >
      {sentiment}
    </span>
  );
}

// ── Importance cell ───────────────────────────────────────────────────────────
function ImportanceCell({ score }: { score: number }) {
  const key: AlphaKey = score >= 80 ? "positive" : score >= 50 ? "warning" : "negative";
  const color = statusColor[key as "positive" | "warning" | "negative"];

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
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
          color,
          fontWeight: 600,
          fontSize: "13px",
          fontFamily: fonts.mono,
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
    cell: ({ row }) => <ImportanceCell score={row.original.importanceScore * 100} />,
  },
  {
    accessorKey: "generatedAt",
    header: "Generated",
    cell: ({ row }) => <DateCell dateStr={row.original.generatedAt} />,
  },
];
