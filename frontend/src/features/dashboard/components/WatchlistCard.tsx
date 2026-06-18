"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import Link from "next/link";
import {
  Search,
  X,
  Plus,
  Trash2,
  Save,
  BarChart3,
  Eye,
  AlertCircle,
  Activity,
  Check,
  Star,
  RotateCcw,
} from "lucide-react";

import {
  useStockLookup,
  useUpdateWatchlist,
  useWatchlist,
} from "../hooks/useWatchlist";
import { colors, fonts, gradients, withAlpha } from "@/theme/theme";

const MAX_SUGGESTIONS = 8;

function sameSet(a: string[], b: string[]) {
  if (a.length !== b.length) return false;
  const set = new Set(a);
  return b.every((x) => set.has(x));
}

export default function WatchlistCard() {
  const { data: watchlist, isLoading: wlLoading, error: wlError } = useWatchlist();
  const { data: lookup, isLoading: luLoading } = useStockLookup();
  const updateMutation = useUpdateWatchlist();

  // Fetch the current watchlist
  const original = useMemo(() => watchlist?.symbols ?? [], [watchlist]);

  const [selected, setSelected] = useState<string[]>([]);
  const [search, setSearch] = useState("");
  const [justSaved, setJustSaved] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  // Adopt current watchlist
  useEffect(() => {
    setSelected(original);
  }, [original]);

  const stocks = lookup?.stockLookups ?? [];

  const nameBySymbol = useMemo(() => {
    const map = new Map<string, string>();
    stocks.forEach((s) => map.set(s.symbol, s.companyName));
    return map;
  }, [stocks]);

  const companyOf = (symbol: string) => nameBySymbol.get(symbol) ?? symbol;

  const suggestions = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return [];
    const chosen = new Set(selected);
    return stocks
      .filter((s) => !chosen.has(s.symbol))
      .filter(
        (s) =>
          s.symbol.toLowerCase().includes(q) ||
          s.companyName.toLowerCase().includes(q)
      )
      .slice(0, MAX_SUGGESTIONS);
  }, [stocks, search, selected]);

  const dirty = !sameSet(selected, original);

  function addSymbol(symbol: string) {
    setSelected((prev) => (prev.includes(symbol) ? prev : [...prev, symbol]));
    setSearch("");
    setJustSaved(false);
    inputRef.current?.focus();
  }

  function removeSymbol(symbol: string) {
    setSelected((prev) => prev.filter((s) => s !== symbol));
    setJustSaved(false);
  }

  function resetToSaved() {
    setSelected(original);
    setJustSaved(false);
  }

  async function save() {
    await updateMutation.mutateAsync(selected);
    setJustSaved(true);
  }

  function onSearchKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === "Enter" && suggestions.length > 0) {
      e.preventDefault();
      addSymbol(suggestions[0].symbol);
    } else if (e.key === "Escape") {
      setSearch("");
    }
  }

  const loading = wlLoading || luLoading;

  // ── Loading ──────────────────────────────────────────────────────────────
  if (loading) {
    return (
      <div
        className="rounded-2xl py-16 flex flex-col items-center gap-3"
        style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
      >
        <Activity size={24} className="animate-pulse" style={{ color: colors.accent }} />
        <span className="text-sm" style={{ fontFamily: fonts.mono, color: colors.textMuted }}>
          Loading your watchlist…
        </span>
      </div>
    );
  }

  // ── Error ────────────────────────────────────────────────────────────────
  if (wlError) {
    return (
      <div
        className="rounded-2xl py-16 flex flex-col items-center gap-3"
        style={{ background: colors.bgSurface, border: `1px solid ${withAlpha("negative", 0.2)}` }}
      >
        <AlertCircle size={24} style={{ color: colors.negative }} />
        <span className="text-sm" style={{ color: colors.textMuted }}>
          Couldn&apos;t load your watchlist.
        </span>
      </div>
    );
  }

  return (
    <div
      className="rounded-2xl overflow-hidden"
      style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
    >
      {/* ── Header ── */}
      <div
        className="px-6 py-5 flex items-center gap-4"
        style={{ background: gradients.header, borderBottom: `1px solid ${colors.border}` }}
      >
        <div className="flex items-center gap-3">
          <div
            className="w-9 h-9 rounded-lg flex items-center justify-center"
            style={{
              background: withAlpha("accent", 0.1),
              border: `1px solid ${withAlpha("accent", 0.2)}`,
            }}
          >
            <Star size={18} style={{ color: colors.accent }} />
          </div>
          <div>
            <h2 className="text-lg font-bold leading-none" style={{ color: colors.textPrimary }}>
              My Watchlist
            </h2>
            <p className="mt-1 text-xs" style={{ fontFamily: fonts.mono, color: colors.textMuted }}>
              {selected.length} {selected.length === 1 ? "stock" : "stocks"}
              {dirty ? " · unsaved changes" : ""}
            </p>
          </div>
        </div>

      </div>

      <div className="p-6 space-y-5">
        {/* ── Search / add ── */}
        <div className="relative">
          <div
            className="flex items-center gap-2 px-3 rounded-lg"
            style={{ background: colors.bgPage, border: `1px solid ${colors.border}` }}
          >
            <Search size={15} style={{ color: colors.textFaint }} />
            <input
              ref={inputRef}
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onKeyDown={onSearchKeyDown}
              placeholder="Search by symbol or company…"
              className="flex-1 bg-transparent py-2.5 text-sm outline-none"
              style={{ color: colors.textPrimary }}
            />
            {search && (
              <button
                onClick={() => setSearch("")}
                aria-label="Clear search"
                style={{ color: colors.textFaint }}
              >
                <X size={15} />
              </button>
            )}
          </div>

          {/* Suggestions dropdown */}
          {suggestions.length > 0 && (
            <div
              className="absolute z-20 mt-2 w-full rounded-lg overflow-hidden shadow-xl"
              style={{ background: colors.bgSurface, border: `1px solid ${colors.border}` }}
            >
              {suggestions.map((stock) => (
                <button
                  key={stock.symbol}
                  onClick={() => addSymbol(stock.symbol)}
                  className="w-full flex items-center justify-between gap-3 px-4 py-2.5 text-left transition-colors"
                  style={{ background: "transparent", borderBottom: `1px solid ${colors.border}` }}
                  onMouseEnter={(e) => (e.currentTarget.style.background = colors.bgHover)}
                  onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")}
                >
                  <span className="min-w-0">
                    <span className="block text-sm truncate" style={{ color: colors.textPrimary }}>
                      {stock.companyName}
                    </span>
                    <span className="block text-xs" style={{ fontFamily: fonts.mono, color: colors.textMuted }}>
                      {stock.symbol}
                    </span>
                  </span>
                  <Plus size={15} style={{ color: colors.accent }} />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* ── Selected list ── */}
        {selected.length === 0 ? (
          <div
            className="rounded-xl py-12 flex flex-col items-center gap-2"
            style={{ background: colors.bgPage, border: `1px dashed ${colors.border}` }}
          >
            <Star size={22} style={{ color: colors.textFaint }} />
            <span className="text-sm" style={{ color: colors.textMuted }}>
              No stocks yet — search above to add some.
            </span>
          </div>
        ) : (
          <div className="space-y-2">
            {selected.map((symbol) => (
              <div
                key={symbol}
                className="group flex items-center justify-between gap-3 px-4 py-3 rounded-xl transition-colors"
                style={{ background: colors.bgPage, border: `1px solid ${colors.border}` }}
                onMouseEnter={(e) => (e.currentTarget.style.borderColor = withAlpha("accent", 0.2))}
                onMouseLeave={(e) => (e.currentTarget.style.borderColor = colors.border)}
              >
                <Link href={`/stocks/${symbol}`} className="flex items-center gap-3 min-w-0 flex-1">
                  <span
                    className="text-xs px-2 py-1 rounded-md font-semibold shrink-0"
                    style={{
                      fontFamily: fonts.mono,
                      color: colors.accent,
                      background: withAlpha("accent", 0.08),
                      border: `1px solid ${withAlpha("accent", 0.2)}`,
                    }}
                  >
                    {symbol}
                  </span>
                  <span className="text-sm truncate" style={{ color: colors.textBody }}>
                    {companyOf(symbol)}
                  </span>
                </Link>

                <div className="flex items-center gap-1 shrink-0">
                  <Link
                    href={`/stocks/${symbol}`}
                    title="Overview"
                    className="w-8 h-8 rounded-lg flex items-center justify-center transition-colors"
                    style={{ color: colors.textMuted }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.color = colors.accent;
                      e.currentTarget.style.background = withAlpha("accent", 0.08);
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.color = colors.textMuted;
                      e.currentTarget.style.background = "transparent";
                    }}
                  >
                    <Eye size={15} />
                  </Link>
                  <Link
                    href={`/stocks/${symbol}/investment-insights`}
                    title="Investment insights"
                    className="w-8 h-8 rounded-lg flex items-center justify-center transition-colors"
                    style={{ color: colors.textMuted }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.color = colors.accent;
                      e.currentTarget.style.background = withAlpha("accent", 0.08);
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.color = colors.textMuted;
                      e.currentTarget.style.background = "transparent";
                    }}
                  >
                    <BarChart3 size={15} />
                  </Link>
                  <button
                    onClick={() => removeSymbol(symbol)}
                    title="Remove"
                    className="w-8 h-8 rounded-lg flex items-center justify-center transition-colors"
                    style={{ color: colors.textMuted }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.color = colors.negative;
                      e.currentTarget.style.background = withAlpha("negative", 0.1);
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.color = colors.textMuted;
                      e.currentTarget.style.background = "transparent";
                    }}
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* ── Footer actions ── */}
        <div className="flex items-center justify-between gap-3 pt-1">
          <span className="text-xs" style={{ fontFamily: fonts.mono, color: colors.textFaint }}>
            {watchlist?.updatedAt
              ? `Last saved ${new Date(watchlist.updatedAt).toLocaleString()}`
              : ""}
          </span>

          <div className="flex items-center gap-2">
            {dirty && (
              <button
                onClick={resetToSaved}
                disabled={updateMutation.isPending}
                className="flex items-center gap-1.5 px-3 py-2 rounded-lg text-xs transition-colors"
                style={{
                  fontFamily: fonts.mono,
                  color: colors.textMuted,
                  background: "transparent",
                  border: `1px solid ${colors.border}`,
                }}
                onMouseEnter={(e) => (e.currentTarget.style.color = colors.textPrimary)}
                onMouseLeave={(e) => (e.currentTarget.style.color = colors.textMuted)}
              >
                <RotateCcw size={13} />
                Reset
              </button>
            )}

            <button
              onClick={save}
              disabled={!dirty || updateMutation.isPending}
              className="flex items-center gap-1.5 px-4 py-2 rounded-lg text-xs font-semibold transition-colors"
              style={{
                fontFamily: fonts.mono,
                color: !dirty && justSaved ? colors.positive : colors.accent,
                background:
                  !dirty && justSaved
                    ? withAlpha("positive", 0.1)
                    : dirty
                    ? withAlpha("accent", 0.12)
                    : withAlpha("accent", 0.05),
                border: `1px solid ${
                  !dirty && justSaved
                    ? withAlpha("positive", 0.25)
                    : withAlpha("accent", 0.2)
                }`,
                cursor: !dirty || updateMutation.isPending ? "default" : "pointer",
                opacity: !dirty && !justSaved ? 0.55 : 1,
              }}
            >
              {updateMutation.isPending ? (
                <>
                  <Activity size={13} className="animate-pulse" />
                  Saving…
                </>
              ) : !dirty && justSaved ? (
                <>
                  <Check size={13} />
                  Saved
                </>
              ) : (
                <>
                  <Save size={13} />
                  Save Watchlist
                </>
              )}
            </button>
          </div>
        </div>

        {updateMutation.isError && (
          <p className="text-xs" style={{ color: colors.negative }}>
            Failed to save — please try again.
          </p>
        )}
      </div>
    </div>
  );
}