"use client";

import {
  AreaChart,
  Area,
  ResponsiveContainer,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ReferenceLine,
  ReferenceDot,
} from "recharts";
import { Activity, TrendingUp, TrendingDown, Minus } from "lucide-react";
import { PriceHistory } from "../types/stock-overview";
import { colors, fonts, statusColor } from "@/theme/theme";
import { useThemeColors } from "@/theme/useThemeColors";

type TrendKey = "positive" | "negative" | "accent";

interface Props {
  history: PriceHistory;
  days: number;
  onDaysChange: (days: number) => void;
}

interface ChartPoint {
  timestamp: number;
  price: number;
  prevPrice: number;
}

interface CustomTooltipProps {
  active?: boolean;
  payload?: { value: number; payload: ChartPoint }[];
  label?: number;
  days: number;
}

const MONO = fonts.mono;

// ── Formatting helpers ──────────────────────────────────────────────────────
function formatAxisTick(timestamp: number, days: number): string {
  const date = new Date(timestamp);

  if (days <= 1) {
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  }
  if (days <= 7) {
    return date.toLocaleDateString([], { weekday: "short", day: "2-digit" });
  }
  if (days <= 90) {
    return date.toLocaleDateString([], { day: "2-digit", month: "short" });
  }
  return date.toLocaleDateString([], { month: "short", year: "2-digit" });
}

function formatTooltipLabel(timestamp: number, days: number): string {
  const date = new Date(timestamp);

  if (days <= 1) {
    return date.toLocaleString([], {
      day: "2-digit",
      month: "short",
      hour: "2-digit",
      minute: "2-digit",
    });
  }
  return date.toLocaleDateString([], {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function dayLabel(days: number) {
  return days === 365 ? "1 year" : `${days} days`;
}

// ── Tooltip (HTML — CSS var() resolves fine here) ────────────────────────────
function CustomTooltip({ active, payload, label, days }: CustomTooltipProps) {
  if (!active || !payload?.length || label === undefined) return null;

  const point = payload[0].payload;
  const delta = point.price - point.prevPrice;
  const deltaColor =
    delta > 0 ? colors.positive : delta < 0 ? colors.negative : colors.textMuted;

  return (
    <div
      style={{
        background: colors.bgSurfaceAlt,
        border: `1px solid ${colors.border}`,
        borderRadius: "8px",
        padding: "8px 12px",
        minWidth: "118px",
      }}
    >
      <div style={{ fontSize: "11px", color: colors.textMuted, fontFamily: MONO }}>
        {formatTooltipLabel(label, days)}
      </div>
      <div
        style={{
          display: "flex",
          alignItems: "baseline",
          justifyContent: "space-between",
          gap: "10px",
          marginTop: "2px",
        }}
      >
        <span style={{ fontSize: "14px", fontWeight: 700, color: colors.textPrimary, fontFamily: MONO }}>
          LKR {point.price.toFixed(2)}
        </span>
        {delta !== 0 && (
          <span style={{ fontSize: "11px", fontWeight: 600, color: deltaColor, fontFamily: MONO }}>
            {delta > 0 ? "+" : ""}
            {delta.toFixed(2)}
          </span>
        )}
      </div>
    </div>
  );
}

export default function PriceHistoryChart({ history, days }: Props) {
  // Resolved (non-var) colors for the SVG/Recharts layer, which can't read var().
  const c = useThemeColors();

  const chartData: ChartPoint[] = history.points.map((point, i) => ({
    timestamp: new Date(point.timestamp).getTime(),
    price: point.price,
    prevPrice: i > 0 ? history.points[i - 1].price : point.price,
  }));

  if (chartData.length === 0) {
    return (
      <div
        className="rounded-xl p-5"
        style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
      >
        <div className="flex items-center gap-2 mb-1">
          <div
            className="w-6 h-6 rounded-md flex items-center justify-center"
            style={{ background: colors.bgSurfaceAlt, color: colors.accent }}
          >
            <Activity size={14} />
          </div>
          <h2 className="text-sm font-semibold" style={{ color: colors.textPrimary }}>
            Price History
          </h2>
        </div>
        <div className="h-[350px] flex items-center justify-center">
          <p className="text-xs" style={{ color: colors.textMuted }}>
            No price data available for this range
          </p>
        </div>
      </div>
    );
  }

  const firstPrice = chartData[0].price;
  const lastPrice = chartData[chartData.length - 1].price;
  const change = lastPrice - firstPrice;
  const changePercent = firstPrice !== 0 ? (change / firstPrice) * 100 : 0;

  const trendKey: TrendKey = change > 0 ? "positive" : change < 0 ? "negative" : "accent";
  const trendVar = statusColor[trendKey]; // CSS var — HTML text
  const trendHex = c[trendKey]; // resolved — SVG
  const TrendIcon = change > 0 ? TrendingUp : change < 0 ? TrendingDown : Minus;

  const maxPoint = chartData.reduce((max, p) => (p.price > max.price ? p : max), chartData[0]);
  const minPoint = chartData.reduce((min, p) => (p.price < min.price ? p : min), chartData[0]);
  const range = maxPoint.price - minPoint.price;
  const padding = range > 0 ? range * 0.12 : Math.max(minPoint.price * 0.01, 0.5);

  const targetTicks = 6;
  const tickInterval = chartData.length > targetTicks ? Math.floor(chartData.length / targetTicks) : 0;

  const lineType = "monotone";

  return (
    <div
      className="rounded-xl p-5"
      style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
    >
      {/* Header */}
      <div className="flex items-start justify-between gap-4 mb-1">
        <div>
          <div className="flex items-center gap-2">
            <div
              className="w-6 h-6 rounded-md flex items-center justify-center"
              style={{ background: colors.bgSurfaceAlt, color: colors.accent }}
            >
              <Activity size={14} />
            </div>
            <h2 className="text-sm font-semibold" style={{ color: colors.textPrimary }}>
              Price History
            </h2>
          </div>
          <p className="text-xs mt-1 ml-8" style={{ color: colors.textMuted }}>
            Last {dayLabel(days)}
          </p>
        </div>

        <div className="text-right">
          <div className="text-lg font-bold" style={{ color: colors.textPrimary, fontFamily: MONO }}>
            LKR {lastPrice.toFixed(2)}
          </div>
          <div
            className="flex items-center justify-end gap-1 text-xs font-semibold mt-0.5"
            style={{ color: trendVar, fontFamily: MONO }}
          >
            <TrendIcon size={12} />
            <span>
              {change >= 0 ? "+" : ""}
              {change.toFixed(2)} ({changePercent >= 0 ? "+" : ""}
              {changePercent.toFixed(2)}%)
            </span>
          </div>
        </div>
      </div>

      {/* Chart */}
      <div className="h-[350px] mt-3">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData} margin={{ top: 18, right: 8, left: 0, bottom: 18 }}>
            <defs>
              <linearGradient id="priceGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={trendHex} stopOpacity={0.25} />
                <stop offset="95%" stopColor={trendHex} stopOpacity={0} />
              </linearGradient>
            </defs>

            <CartesianGrid strokeDasharray="3 3" stroke={c.border} vertical={false} />

            <XAxis
              dataKey="timestamp"
              type="category"
              interval={tickInterval}
              stroke={c.border}
              tick={{ fill: c.textFaint, fontSize: 11, fontFamily: MONO }}
              tickLine={false}
              tickFormatter={(value: number) => formatAxisTick(value, days)}
            />

            <YAxis
              stroke={c.border}
              tick={{ fill: c.textFaint, fontSize: 11, fontFamily: MONO }}
              tickLine={false}
              axisLine={false}
              domain={[minPoint.price - padding, maxPoint.price + padding]}
              width={56}
              tickFormatter={(value: number) => value.toFixed(0)}
            />

            <Tooltip
              content={(props) => (
                <CustomTooltip
                  active={props.active}
                  payload={props.payload as CustomTooltipProps["payload"]}
                  label={props.label as number | undefined}
                  days={days}
                />
              )}
              cursor={{ stroke: trendHex, strokeOpacity: 0.33 }}
            />

            <ReferenceLine
              y={firstPrice}
              stroke={c.textMuted}
              strokeDasharray="4 4"
              strokeOpacity={0.5}
              label={{
                value: `Start ${firstPrice.toFixed(2)}`,
                position: "insideBottomLeft",
                fill: c.textMuted,
                fontSize: 10,
                fontFamily: MONO,
              }}
            />

            <Area
              type={lineType}
              dataKey="price"
              stroke={trendHex}
              strokeWidth={2}
              fill="url(#priceGradient)"
              activeDot={{ r: 4, fill: trendHex, stroke: c.bgPage, strokeWidth: 2 }}
              isAnimationActive={false}
            />

            <ReferenceDot
              x={maxPoint.timestamp}
              y={maxPoint.price}
              r={3}
              fill={c.positive}
              stroke={c.bgPage}
              strokeWidth={2}
              label={{ value: `H ${maxPoint.price.toFixed(2)}`, position: "top", fill: c.positive, fontSize: 10, fontFamily: MONO }}
            />
            <ReferenceDot
              x={minPoint.timestamp}
              y={minPoint.price}
              r={3}
              fill={c.negative}
              stroke={c.bgPage}
              strokeWidth={2}
              label={{ value: `L ${minPoint.price.toFixed(2)}`, position: "bottom", fill: c.negative, fontSize: 10, fontFamily: MONO }}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
