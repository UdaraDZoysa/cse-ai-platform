"use client";

import { Moon, Sun } from "lucide-react";

import { useTheme } from "@/providers/ThemeProvider";
import { colors, withAlpha } from "@/theme/theme";

export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();
  const isDark = theme === "dark";

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label={isDark ? "Switch to light mode" : "Switch to dark mode"}
      title={isDark ? "Switch to light mode" : "Switch to dark mode"}
      className="fixed bottom-6 right-6 z-50 flex h-11 w-11 items-center justify-center rounded-full transition-colors"
      style={{
        background: colors.bgSurface,
        border: `1px solid ${colors.border}`,
        color: colors.accent,
        boxShadow: "0 6px 24px rgba(0, 0, 0, 0.28)",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.borderColor = withAlpha("accent", 0.45);
        e.currentTarget.style.background = colors.bgSurfaceAlt;
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.borderColor = colors.border;
        e.currentTarget.style.background = colors.bgSurface;
      }}
    >
      {isDark ? <Sun size={18} /> : <Moon size={18} />}
    </button>
  );
}
