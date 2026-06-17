import { TrendingUp, TrendingDown, BarChart3, Layers, DollarSign } from "lucide-react";
import { StockOverview } from "../types/stock-overview";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

interface Props {
  overview: StockOverview;
}

function formatCompact(num: number) {
  if (num >= 1e9) return (num / 1e9).toFixed(2) + "B";
  if (num >= 1e6) return (num / 1e6).toFixed(2) + "M";
  if (num >= 1e3) return (num / 1e3).toFixed(2) + "K";
  return num.toLocaleString();
}

export default function StockOverviewCard({ overview }: Props) {
  const change = Number(overview.percentageChange);
  const isPositive = change >= 0;
  const changeColor = isPositive ? colors.positive : colors.negative;
  const ChangeIcon = isPositive ? TrendingUp : TrendingDown;

  const stats = [
    {
      label: "Current Price",
      value: `LKR ${Number(overview.currentPrice).toFixed(2)}`,
      icon: <DollarSign size={12} />,
      color: colors.accent,
      valueColor: colors.textPrimary,
    },
    {
      label: "Change",
      value: `${isPositive ? "+" : ""}${change.toFixed(2)}%`,
      icon: <ChangeIcon size={12} />,
      color: changeColor,
      valueColor: changeColor,
    },
    {
      label: "Volume",
      value: formatCompact(Number(overview.shareVolume)),
      icon: <BarChart3 size={12} />,
      color: colors.info,
      valueColor: colors.textPrimary,
    },
    {
      label: "Market Cap",
      value: `LKR ${formatCompact(Number(overview.marketCap))}`,
      icon: <Layers size={12} />,
      color: colors.warning,
      valueColor: colors.textPrimary,
    },
  ];

  return (
    <div
      className="rounded-2xl p-7"
      style={{ background: gradients.header, border: `1px solid ${colors.border}` }}
    >
      {/* Header row */}
      <div className="flex items-start justify-between gap-6 flex-wrap">
        <div>
          <h1
            className="text-5xl font-bold leading-none"
            style={{ fontFamily: fonts.mono, color: colors.accent }}
          >
            {overview.symbol}
          </h1>
          <p className="text-sm mt-3" style={{ color: colors.textMuted }}>
            {overview.companyName}
          </p>
        </div>

        {/* Live indicator */}
        <div
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg"
          style={{
            background: withAlpha("positive", 0.07),
            border: `1px solid ${withAlpha("positive", 0.2)}`,
          }}
        >
          <span
            className="w-1.5 h-1.5 rounded-full"
            style={{ background: colors.positive }}
          />
          <span
            className="text-xs font-semibold"
            style={{ fontFamily: fonts.mono, color: colors.positive }}
          >
            LIVE
          </span>
        </div>
      </div>

      {/* Stats grid */}
      <div className="grid md:grid-cols-4 gap-4 mt-7">
        {stats.map(({ label, value, icon, color, valueColor }) => (
          <div
            key={label}
            className="p-4 rounded-lg"
            style={{ background: colors.bgPage, border: `1px solid ${colors.border}` }}
          >
            <div className="flex items-center gap-2 mb-2">
              <span style={{ color }}>{icon}</span>
              <span
                className="text-xs uppercase tracking-widest"
                style={{ color: colors.textFaint }}
              >
                {label}
              </span>
            </div>
            <div
              className="text-2xl font-bold"
              style={{ fontFamily: fonts.mono, color: valueColor }}
            >
              {value}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
