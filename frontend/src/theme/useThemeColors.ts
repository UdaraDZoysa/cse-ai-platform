"use client";

import { useEffect, useState } from "react";

import { useTheme } from "@/providers/ThemeProvider";

// SVG presentation attributes (stroke / fill on <circle>, <path>, Recharts
// series, etc.) do NOT understand CSS `var()`. For those few spots we resolve
// the token to a concrete color at runtime, and recompute whenever the theme
// flips. Everywhere else, plain CSS `var()` in inline styles is fine.

export interface ResolvedPalette {
  accent: string;
  info: string;
  positive: string;
  negative: string;
  warning: string;
  border: string;
  bgPage: string;
  bgSurface: string;
  bgSurfaceAlt: string;
  textPrimary: string;
  textBody: string;
  textMuted: string;
  textFaint: string;
}

const VAR_MAP: Record<keyof ResolvedPalette, string> = {
  accent: "--accent",
  info: "--info",
  positive: "--positive",
  negative: "--negative",
  warning: "--warning",
  border: "--border-c",
  bgPage: "--bg-page",
  bgSurface: "--bg-surface",
  bgSurfaceAlt: "--bg-surface-alt",
  textPrimary: "--text-primary",
  textBody: "--text-body",
  textMuted: "--text-muted",
  textFaint: "--text-faint",
};

// Dark defaults — used for SSR / first paint before getComputedStyle runs.
const DARK_FALLBACK: ResolvedPalette = {
  accent: "#00d4ff",
  info: "#4d9eff",
  positive: "#22c55e",
  negative: "#ff4560",
  warning: "#ffb800",
  border: "#1a2744",
  bgPage: "#0a0e1a",
  bgSurface: "#0f1629",
  bgSurfaceAlt: "#141e35",
  textPrimary: "#e8eef8",
  textBody: "#c7d2e5",
  textMuted: "#6b7fa3",
  textFaint: "#3d5080",
};

function readPalette(): ResolvedPalette {
  if (typeof window === "undefined") return DARK_FALLBACK;
  const cs = getComputedStyle(document.documentElement);
  const out = {} as ResolvedPalette;
  (Object.keys(VAR_MAP) as (keyof ResolvedPalette)[]).forEach((key) => {
    out[key] = cs.getPropertyValue(VAR_MAP[key]).trim() || DARK_FALLBACK[key];
  });
  return out;
}

/** Concrete (non-var) token colors that update on theme change. */
export function useThemeColors(): ResolvedPalette {
  const { theme } = useTheme();
  const [palette, setPalette] = useState<ResolvedPalette>(readPalette);

  useEffect(() => {
    // Defer one frame so the .dark/.light class swap has applied before we read.
    const id = requestAnimationFrame(() => setPalette(readPalette()));
    return () => cancelAnimationFrame(id);
  }, [theme]);

  return palette;
}
