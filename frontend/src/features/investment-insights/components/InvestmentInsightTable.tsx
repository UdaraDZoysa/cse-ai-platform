"use client";

import { columns } from "./columns";
import { DataTable } from "./data-table";
import { InvestmentInsightSummary } from "../types/investment-insight.types";
import { useRouter } from "next/navigation";

interface Props {
  insights: InvestmentInsightSummary[];
}

export default function InvestmentInsightTable({
    insights,
}: Props) {

    const router = useRouter();

    return (
        <DataTable
            columns={columns}
            data={insights}
            onRowClick={(insight) =>
                router.push(
                    `/investment-insights/${insight.id}`
                )
            }
        />
    );
}