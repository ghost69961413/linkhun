import { QueryClient } from '@tanstack/react-query';
import axios from 'axios';

export function retryTransientRequest(failureCount: number, error: unknown) {
  if (failureCount >= 1 || !axios.isAxiosError(error)) return false;
  const status = error.response?.status;
  return !status || status === 502 || status === 503 || status === 504 || error.code === 'ECONNABORTED';
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: { staleTime: 30_000, gcTime: 5 * 60_000, retry: retryTransientRequest, retryDelay: 2_000, refetchOnWindowFocus: false },
    mutations: { retry: 0 },
  },
});
