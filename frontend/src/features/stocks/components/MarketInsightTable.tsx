"use client";

import { useRouter } from "next/navigation";

import { DataTable }
from "@/features/investment-insights/components/data-table";

import { marketInsightColumns }
from "./market-insight-columns";

import { MarketInsightHistoryResponse }
from "../types/stock-overview";

interface Props {
  insights: MarketInsightHistoryResponse[];
}

export default function MarketInsightTable({
  insights,
}: Props) {

  const router = useRouter();

  return (
    <DataTable
      columns={marketInsightColumns}
      data={insights}
      onRowClick={(insight) =>
        router.push(
          `/market-insights/${insight.id}`
        )
      }
    />
  );
}