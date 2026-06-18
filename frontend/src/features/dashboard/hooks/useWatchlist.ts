import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  getStockLookup,
  getWatchList,
  updateWatchList,
} from "../api/watchlist-api";

export function useWatchlist() {
  return useQuery({
    queryKey: ["watchlist"],
    queryFn: getWatchList,
  });
}

export function useStockLookup() {
  return useQuery({
    queryKey: ["stock-lookup"],
    queryFn: getStockLookup,
    staleTime: 1000 * 60 * 60,
  });
}

export function useUpdateWatchlist() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateWatchList,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["watchlist"] });
    },
  });
}
