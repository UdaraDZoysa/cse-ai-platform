export interface InvestmentInsightDetail {
  id: string;
  symbol: string;
  companyName: string;              
  action: string;
  opportunityScore: number;
  riskLevel: string;
  executiveSummary: string;
  marketReasoning: string;
  actionReasoning: string;
  confidenceReasoning: string;
  supportingFactors: string[];
  risks: string[];
  invalidationConditions: string[];
  createdAt: string;
}