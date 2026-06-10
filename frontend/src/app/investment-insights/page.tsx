"use client";

import { useInvestmentInsights } from "@/features/investment-insights/hooks/useInvestmentInsights";
import Link from "next/link";

export default function InvestmentInsightsPage() {
  const { data, isLoading, error } =
    useInvestmentInsights(0, 10);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Failed to load insights.</div>;
  }

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">
        Investment Insights
      </h1>

      <table className="w-full border">
        <thead>
          <tr>
            <th>Symbol</th>
            <th>Action</th>
            <th>Opportunity Score</th>
            <th>Risk</th>
            <th>Created</th>
          </tr>
        </thead>

        <tbody>
          {data?.content.map((insight) => (
            <tr key={insight.id}>
              <td>{insight.symbol}</td>
              <td>{insight.action}</td>
              <td>{insight.opportunityScore}</td>
              <td>{insight.riskLevel}</td>
              <td>{insight.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="mt-4 align-middle">    
        <button className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition-colors ">
          <Link href="/">
            Back to Home
          </Link>
        </button>
      </div>          
    </div>
  );
}