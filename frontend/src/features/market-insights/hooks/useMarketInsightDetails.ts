import { useQuery } from '@tanstack/react-query';
import {
    getMarketInsightDetails
} from '../api/market-insight-api';

export function useMarketInsightDetails(
    id: string
) {
    return useQuery({
        queryKey: ['market-insight', id],
        queryFn: () => getMarketInsightDetails(id),
    });
}