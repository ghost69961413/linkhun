import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from '../services/api';
import type { ApiEnvelope, ConnectionRequest, ConnectionUser } from '../types';

export const connectionKeys = {
  all: ['connections'] as const,
  pending: () => [...connectionKeys.all, 'pending'] as const,
  sent: () => [...connectionKeys.all, 'sent'] as const,
  accepted: () => [...connectionKeys.all, 'accepted'] as const,
  suggestions: () => [...connectionKeys.all, 'suggestions'] as const,
  search: (query: string) => [...connectionKeys.all, 'search', query] as const,
};

async function list<T>(path: string) {
  const { data } = await api.get<ApiEnvelope<T[]> | T[]>(path);
  return Array.isArray(data) ? data : data.data;
}
export const usePendingConnections = (enabled = true) => useQuery({ queryKey: connectionKeys.pending(), queryFn: () => list<ConnectionRequest>('/connections/pending'), enabled });
export const useSentConnections = (enabled = true) => useQuery({ queryKey: connectionKeys.sent(), queryFn: () => list<ConnectionRequest>('/connections/sent'), enabled });
export const useAcceptedConnections = (enabled = true) => useQuery({ queryKey: connectionKeys.accepted(), queryFn: () => list<ConnectionRequest>('/connections'), enabled });
interface UserSearchResult { id?: number; userId?: number; username: string; firstName?: string; lastName?: string; fullName?: string; headline?: string | null; profilePicture?: string | null }
interface SearchPage<T> { content: T[] }
export const useConnectionSuggestions = (query = '', enabled = true) => useQuery({
  queryKey: connectionKeys.search(query),
  queryFn: async () => {
    const { data } = await api.get<ApiEnvelope<SearchPage<UserSearchResult>>>(`/search/users`, { params: { query, page: 0, size: 30 } });
    return (data.data.content ?? []).map(user => ({
      userId: user.userId ?? user.id ?? 0,
      username: user.username,
      fullName: user.fullName ?? ([user.firstName, user.lastName].filter(Boolean).join(' ') || user.username),
      headline: user.headline,
      profileImage: user.profilePicture,
    } satisfies ConnectionUser));
  },
  enabled,
  staleTime: 60_000,
});
export function useConnectionAction(action: 'accept' | 'reject' | 'withdraw') {
  const client = useQueryClient();
  return useMutation({
    mutationFn: async (requestId: number) => {
      if (action === 'withdraw') return api.delete(`/connections/withdraw/${requestId}`);
      return api.post(`/connections/${action}/${requestId}`);
    },
    onSuccess: async () => { await client.invalidateQueries({ queryKey: connectionKeys.all }); },
  });
}
export async function requestConnection(userId: number) {
  const { data } = await api.post<ApiEnvelope<ConnectionRequest>>(`/connections/request/${userId}`);
  return data.data;
}
export async function getMutualConnections(userId: number) {
  const { data } = await api.get<ApiEnvelope<ConnectionUser[]>>(`/connections/mutual/${userId}`);
  return data.data;
}

export const useMutualConnections = (userId: number, enabled = true) => useQuery({ queryKey: [...connectionKeys.all, 'mutual', userId], queryFn: () => getMutualConnections(userId), enabled: enabled && Number.isFinite(userId) && userId > 0 });

export function useSendConnection() {
  const client = useQueryClient();
  return useMutation({ mutationFn: requestConnection, onSuccess: async () => { await client.invalidateQueries({ queryKey: connectionKeys.all }); } });
}
