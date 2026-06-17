// ─────────────────────────────────────────────────────────────────────────────
// Central design tokens for the CSE Investment Intelligence Platform.
//
// Every value resolves to a CSS custom property declared in globals.css.
// Those properties swap automatically between the `.dark` and `.light`
// classes on <html> (toggled by ThemeProvider), so using these tokens in an
// inline `style={{ ... }}` is enough to make a component theme-aware — no
// per-component dark/light branching required.
//
// Usage:
//   import { colors, fonts, withAlpha } from "@/theme/theme";
//   <div style={{ background: colors.bgSurface, color: colors.textBody }} />
//   <span style={{ color: colors.accent, fontFamily: fonts.mono }} />
//   <div style={{ background: withAlpha("accent", 0.15) }} />   // translucent fill
// ─────────────────────────────────────────────────────────────────────────────

export const colors = {
  // Backgrounds / surfaces
  bgPage: "var(--bg-page)",
  bgSurface: "var(--bg-surface)",
  bgSurfaceAlt: "var(--bg-surface-alt)",
  bgSurface2: "var(--bg-surface-2)",
  bgHover: "var(--bg-hover)",
  border: "var(--border-c)",

  // Text
  textPrimary: "var(--text-primary)", // headings, strong values, table cells
  textBody: "var(--text-body)", // paragraph / prose
  textMuted: "var(--text-muted)", // labels, secondary
  textFaint: "var(--text-faint)", // tertiary / axis / ellipsis
  textEyebrow: "var(--text-eyebrow)", // uppercase section eyebrows

  // Accents / status
  accent: "var(--accent)", // cyan — primary accent
  info: "var(--info)", // blue — secondary accent
  positive: "var(--positive)", // green
  negative: "var(--negative)", // red
  warning: "var(--warning)", // amber
} as const;

export const fonts = {
  sans: "var(--font-sans)",
  mono: "var(--font-mono)",
} as const;

// Gradient used by hero / header cards.
export const gradients = {
  header: "linear-gradient(135deg, var(--bg-surface), var(--bg-surface-alt))",
} as const;

// ── Translucent overlays ─────────────────────────────────────────────────────
// rgb triples (e.g. "0, 212, 255") declared in globals.css, so we can build
// rgba() fills/glows/borders that still respond to the active theme.
const rgb = {
  accent: "var(--accent-rgb)",
  info: "var(--info-rgb)",
  positive: "var(--positive-rgb)",
  negative: "var(--negative-rgb)",
  warning: "var(--warning-rgb)",
  page: "var(--bg-page-rgb)",
} as const;

export type AlphaKey = keyof typeof rgb;

/** Build a theme-aware translucent color, e.g. withAlpha("accent", 0.15). */
export function withAlpha(key: AlphaKey, alpha: number): string {
  return `rgba(${rgb[key]}, ${alpha})`;
}

/** Solid status color by semantic key — handy for data-driven badges/charts. */
export const statusColor: Record<
  "positive" | "negative" | "warning" | "accent" | "info",
  string
> = {
  positive: colors.positive,
  negative: colors.negative,
  warning: colors.warning,
  accent: colors.accent,
  info: colors.info,
};
