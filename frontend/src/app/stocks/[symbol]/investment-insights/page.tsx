"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import Link from "next/link";

import InvestmentInsightTable
from "@/features/investment-insights/components/InvestmentInsightTable";

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";

import { useInvestmentInsightSummaryForStock }
from "@/features/stocks/hooks/useStockOverview";

export default function StockInvestmentInsightsPage() {

  const params = useParams();

  const symbol = params.symbol as string;

  const [page, setPage] = useState(0);

  const {
    data,
    isLoading,
    error,
  } = useInvestmentInsightSummaryForStock(
    symbol,
    page,
    10
  );

  if (isLoading) {
    return (
      <div className="p-8">
        Loading...
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="p-8">
        Failed to load investment insights.
      </div>
    );
  }

  return (
    <div className="p-8 max-w-7xl mx-auto">

      <Link
        href={`/stocks/${symbol}`}
        className="text-sm text-muted-foreground hover:underline"
      >
        ← Back
      </Link>

      <h1 className="text-3xl font-bold mt-4">
        Investment Insights
      </h1>

      <p className="text-muted-foreground mt-2">
        {symbol} • {data.totalElements} total insights
      </p>

      <div className="mt-6">
        <InvestmentInsightTable
          insights={data.content}
        />
      </div>

      <div className="flex justify-between items-center mt-6">

        <div className="text-sm text-muted-foreground">
          Page {data.number + 1} of {data.totalPages}
        </div>

        <Pagination>
          <PaginationContent>

            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();

                  if (!data.first) {
                    setPage(page - 1);
                  }
                }}
              />
            </PaginationItem>

            <PaginationItem>
              <PaginationLink isActive>
                {page + 1}
              </PaginationLink>
            </PaginationItem>

            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();

                  if (!data.last) {
                    setPage(page + 1);
                  }
                }}
              />
            </PaginationItem>

          </PaginationContent>
        </Pagination>

      </div>

    </div>
  );
}