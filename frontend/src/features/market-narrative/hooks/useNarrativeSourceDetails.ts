import { useQuery } from '@tanstack/react-query';
import {
    getNarrativeDetails
} from '../api/narrative-source-api';

export function useNarrativeDetails(
    id: string
) {
    return useQuery({
        queryKey: ['narrative-details', id],
        queryFn: () => getNarrativeDetails(id),
        enabled: !!id,
    });
}
