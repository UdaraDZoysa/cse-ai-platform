"use client";

import Link from "next/link";
import { ArrowRight, FileText } from "lucide-react";

import { MarketNarratives } from "../types/stock-overview";
import { colors, withAlpha } from "@/theme/theme";

interface Props {
  narrative: MarketNarratives;
}

export default function MarketNarrativeCard({ narrative }: Props) {
  return (
    <div>
      {/* Section Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <div
            className="w-6 h-6 rounded-md flex items-center justify-center"
            style={{ background: withAlpha("accent", 0.1), color: colors.accent }}
          >
            <FileText size={14} />
          </div>
          <h2 className="text-base font-semibold" style={{ color: colors.textPrimary }}>
            Market Narrative
          </h2>
        </div>

        <Link
          href={`/market-narrative/${narrative.id}`}
          className="flex items-center gap-1 text-xs transition-colors"
          style={{ color: colors.accent }}
        >
          View Sources <ArrowRight size={12} />
        </Link>
      </div>

      <div
        className="rounded-xl px-6 py-6"
        style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
      >
        <p className="leading-8" style={{ color: colors.textBody }}>
          {narrative.summary}
        </p>

        <p className="mt-6 text-xs" style={{ color: colors.textMuted }}>
          Generated {new Date(narrative.generatedAt).toLocaleString()}
        </p>
      </div>
    </div>
  );
}
