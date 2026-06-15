import { TrendingUp, TrendingDown, BarChart3, Layers, DollarSign } from "lucide-react";
import { StockOverview } from "../types/stock-overview";

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
  const changeColor = isPositive ? "#00FF94" : "#FF4560";
  const ChangeIcon = isPositive ? TrendingUp : TrendingDown;

  const stats = [
    {
      label: "Current Price",
      value: `LKR ${Number(overview.currentPrice).toFixed(2)}`,
      icon: <DollarSign size={12} />,
      color: "#00D4FF",
      valueColor: "#E8EEF8",
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
      color: "#4D9EFF",
      valueColor: "#E8EEF8",
    },
    {
      label: "Market Cap",
      value: `LKR ${formatCompact(Number(overview.marketCap))}`,
      icon: <Layers size={12} />,
      color: "#FFB800",
      valueColor: "#E8EEF8",
    },
  ];

  return (
    <div
      className="rounded-2xl p-7"
      style={{
        background: "linear-gradient(135deg, #0F1629, #141e35)",
        border: "1px solid #1a2744",
      }}
    >
      {/* Header row */}
      <div className="flex items-start justify-between gap-6 flex-wrap">
        <div>
          <h1
            className="text-5xl font-bold leading-none"
            style={{ fontFamily: "'JetBrains Mono', monospace", color: "#00D4FF" }}
          >
            {overview.symbol}
          </h1>
          <p className="text-sm mt-3" style={{ color: "#6B7FA3" }}>
            {overview.companyName}
          </p>
        </div>

        {/* Live indicator */}
        <div
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg"
          style={{ background: "#00FF9412", border: "1px solid #00FF9433" }}
        >
          <span
            className="w-1.5 h-1.5 rounded-full"
            style={{ background: "#00FF94" }}
          />
          <span
            className="text-xs font-semibold"
            style={{ fontFamily: "'JetBrains Mono', monospace", color: "#00FF94" }}
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
            style={{ background: "#0A0E1A", border: "1px solid #1a2744" }}
          >
            <div className="flex items-center gap-2 mb-2">
              <span style={{ color }}>{icon}</span>
              <span
                className="text-xs uppercase tracking-widest"
                style={{ color: "#3D5080" }}
              >
                {label}
              </span>
            </div>
            <div
              className="text-2xl font-bold"
              style={{ fontFamily: "'JetBrains Mono', monospace", color: valueColor }}
            >
              {value}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}