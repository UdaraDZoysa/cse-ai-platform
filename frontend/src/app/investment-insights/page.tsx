"use client";

import { useState } from "react";
import Link from "next/link";
import { ChevronLeft, ChevronRight, Sparkles, Activity, AlertCircle, Inbox } from "lucide-react";

import InvestmentInsightTable from "@/features/investment-insights/components/InvestmentInsightTable";
import { useInvestmentInsights } from "@/features/investment-insights/hooks/useInvestmentInsights";

export default function InvestmentInsightsPage() {
  const [page, setPage] = useState(0);

  const { data, isLoading, error } = useInvestmentInsights(page, 10);

  const pageNumbers = data
    ? Array.from({ length: data.totalPages }, (_, index) => index).filter(
        (p) =>
          p === 0 ||
          p === data.totalPages - 1 ||
          Math.abs(p - page) <= 2
      )
    : [];

  return (
    <div className="min-h-screen" style={{ background: "#0A0E1A" }}>
      {/* ── Top Bar ── */}
      <div
        className="sticky top-0 z-10 px-8 py-4"
        style={{
          background: "#0A0E1Aee",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid #1a2744",
        }}
      >
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <Link
            href="/"
            className="flex items-center gap-2 text-sm transition-colors"
            style={{ color: "#6B7FA3" }}
            onMouseEnter={(e) => (e.currentTarget.style.color = "#E8EEF8")}
            onMouseLeave={(e) => (e.currentTarget.style.color = "#6B7FA3")}
          >
            <ChevronLeft size={14} />
            Dashboard
          </Link>

          {data && (
            <div
              className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-lg"
              style={{
                fontFamily: "'JetBrains Mono', monospace",
                color: "#6B7FA3",
                background: "#0F1629",
                border: "1px solid #1a2744",
              }}
            >
              <Sparkles size={11} style={{ color: "#00D4FF" }} />
              {data.totalElements} total insights
            </div>
          )}
        </div>
      </div>

      <div className="px-8 py-8 max-w-7xl mx-auto space-y-6">
        {/* ── Page Header ── */}
        <div
          className="rounded-2xl px-7 py-6"
          style={{
            background: "linear-gradient(135deg, #0F1629, #141e35)",
            border: "1px solid #1a2744",
          }}
        >
          <div className="flex items-center gap-3 mb-2">
            <div
              className="w-8 h-8 rounded-lg flex items-center justify-center"
              style={{ background: "#00D4FF18", border: "1px solid #00D4FF33" }}
            >
              <Sparkles size={16} style={{ color: "#00D4FF" }} />
            </div>
            <h1
              className="text-2xl font-bold"
              style={{ color: "#E8EEF8" }}
            >
              Investment Insights
            </h1>
          </div>
          <p className="text-sm" style={{ color: "#6B7FA3" }}>
            AI-generated investment reviews and recommendations
          </p>
        </div>

        {/* ── Loading ── */}
        {isLoading && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{ background: "#0F1629", border: "1px solid #1a2744" }}
          >
            <Activity
              size={24}
              className="animate-pulse"
              style={{ color: "#00D4FF" }}
            />
            <span
              className="text-sm"
              style={{
                fontFamily: "'JetBrains Mono', monospace",
                color: "#6B7FA3",
              }}
            >
              Loading investment insights…
            </span>
          </div>
        )}

        {/* ── Error ── */}
        {error && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{
              background: "#0F1629",
              border: "1px solid #FF456033",
            }}
          >
            <AlertCircle size={24} style={{ color: "#FF4560" }} />
            <span className="text-sm" style={{ color: "#6B7FA3" }}>
              Failed to load investment insights.
            </span>
          </div>
        )}

        {/* ── Empty ── */}
        {!isLoading && !error && data && data.content.length === 0 && (
          <div
            className="rounded-xl py-16 flex flex-col items-center gap-3"
            style={{ background: "#0F1629", border: "1px solid #1a2744" }}
          >
            <Inbox size={24} style={{ color: "#3D5080" }} />
            <span className="text-sm" style={{ color: "#6B7FA3" }}>
              No investment insights found.
            </span>
          </div>
        )}

        {/* ── Data ── */}
        {!isLoading && !error && data && data.content.length > 0 && (
          <>
            {/* Table container */}
            <div
              className="rounded-xl overflow-hidden"
              style={{ border: "1px solid #1a2744" }}
            >
              <InvestmentInsightTable insights={data.content} />
            </div>

            {/* ── Pagination footer ── */}
            <div
              className="rounded-xl px-5 py-4 flex items-center justify-between"
              style={{ background: "#0F1629", border: "1px solid #1a2744" }}
            >
              {/* Page info */}
              <span
                className="text-xs"
                style={{
                  fontFamily: "'JetBrains Mono', monospace",
                  color: "#3D5080",
                }}
              >
                Page{" "}
                <span style={{ color: "#6B7FA3" }}>{data.number + 1}</span>
                {" "}of{" "}
                <span style={{ color: "#6B7FA3" }}>{data.totalPages}</span>
                {"  ·  "}
                <span style={{ color: "#6B7FA3" }}>{data.totalElements}</span>{" "}
                insights
              </span>

              {/* Page controls */}
              <div className="flex items-center gap-1">
                {/* Prev */}
                <button
                  onClick={() => !data.first && setPage((p) => p - 1)}
                  disabled={data.first}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs transition-all"
                  style={{
                    fontFamily: "'JetBrains Mono', monospace",
                    color: data.first ? "#3D5080" : "#6B7FA3",
                    background: "transparent",
                    border: "1px solid #1a2744",
                    cursor: data.first ? "not-allowed" : "pointer",
                  }}
                  onMouseEnter={(e) => {
                    if (!data.first)
                      e.currentTarget.style.color = "#E8EEF8";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = data.first
                      ? "#3D5080"
                      : "#6B7FA3";
                  }}
                >
                  <ChevronLeft size={13} />
                  Prev
                </button>

                {/* Page numbers */}
                <div className="flex items-center gap-1 mx-1">
                  {pageNumbers.map((pageNumber, i) => {
                    const isActive = pageNumber === page;
                    const prevPage = pageNumbers[i - 1];
                    const showEllipsis =
                      prevPage !== undefined && pageNumber - prevPage > 1;

                    return (
                      <span key={pageNumber} className="flex items-center gap-1">
                        {showEllipsis && (
                          <span
                            className="px-1 text-xs"
                            style={{
                              fontFamily: "'JetBrains Mono', monospace",
                              color: "#3D5080",
                            }}
                          >
                            …
                          </span>
                        )}
                        <button
                          onClick={() => setPage(pageNumber)}
                          className="w-8 h-8 rounded-lg text-xs font-semibold transition-all"
                          style={{
                            fontFamily: "'JetBrains Mono', monospace",
                            color: isActive ? "#00D4FF" : "#6B7FA3",
                            background: isActive ? "#00D4FF15" : "transparent",
                            border: isActive
                              ? "1px solid #00D4FF33"
                              : "1px solid transparent",
                          }}
                          onMouseEnter={(e) => {
                            if (!isActive)
                              e.currentTarget.style.color = "#E8EEF8";
                          }}
                          onMouseLeave={(e) => {
                            if (!isActive)
                              e.currentTarget.style.color = "#6B7FA3";
                          }}
                        >
                          {pageNumber + 1}
                        </button>
                      </span>
                    );
                  })}
                </div>

                {/* Next */}
                <button
                  onClick={() => !data.last && setPage((p) => p + 1)}
                  disabled={data.last}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs transition-all"
                  style={{
                    fontFamily: "'JetBrains Mono', monospace",
                    color: data.last ? "#3D5080" : "#6B7FA3",
                    background: "transparent",
                    border: "1px solid #1a2744",
                    cursor: data.last ? "not-allowed" : "pointer",
                  }}
                  onMouseEnter={(e) => {
                    if (!data.last) e.currentTarget.style.color = "#E8EEF8";
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.color = data.last
                      ? "#3D5080"
                      : "#6B7FA3";
                  }}
                >
                  Next
                  <ChevronRight size={13} />
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}