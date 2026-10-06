import api from './api';
import type { Comment, PageResult, Post } from '../types/post';

function unwrap<T>(value: T | { data: T }): T {
  return value && typeof value === 'object' && 'data' in value ? (value as { data: T }).data : value as T;
}

export const postService = {
  async getFeed(page: number, size = 10): Promise<PageResult<Post>> {
    const response = await api.get<PageResult<Post> | { data: PageResult<Post> } | Post[]>('/posts/feed', { params: { page, size } });
    const result = unwrap(response.data);
    return Array.isArray(result) ? { content: result, number: page, last: result.length < size } : result;
  },
  async getByUser(username: string, page = 0, size = 10): Promise<PageResult<Post>> {
    const response = await api.get<PageResult<Post> | { data: PageResult<Post> }>(`/posts/user/${encodeURIComponent(username)}`, { params: { page, size } });
    return unwrap(response.data);
  },
  async getMedia(postId: number, kind: 'image' | 'video'): Promise<Blob> {
    const response = await api.get<Blob>(`/posts/${postId}/media/${kind}`, { responseType: 'blob' });
    return response.data;
  },
  async create(input: { content: string; visibility: string; media?: File | null }): Promise<Post> {
    const form = new FormData();
    form.append('content', input.content);
    form.append('visibility', input.visibility);
    if (input.media) form.append(input.media.type.startsWith('video/') ? 'video' : 'image', input.media);
    const response = await api.post<Post | { data: Post }>('/posts', form, { headers: { 'Content-Type': 'multipart/form-data' } });
    return unwrap(response.data);
  },
  async like(postId: number) {
    try {
      const response = await api.post<{ data?: { liked?: boolean; likeCount?: number }; liked?: boolean; likeCount?: number }>(`/posts/${postId}/likes`);
      return unwrap(response.data) ?? {};
    } catch (error: any) {
      if (error.response?.status !== 404) throw error;
      const response = await api.post<{ data?: { liked?: boolean; likeCount?: number }; liked?: boolean; likeCount?: number }>(`/posts/${postId}/like`);
      return unwrap(response.data) ?? {};
    }
  },
  async unlike(postId: number) {
    try {
      const response = await api.delete<{ data?: { liked?: boolean; likeCount?: number }; liked?: boolean; likeCount?: number }>(`/posts/${postId}/likes`);
      return unwrap(response.data) ?? {};
    } catch (error: any) {
      if (error.response?.status !== 404) throw error;
      const response = await api.delete<{ data?: { liked?: boolean; likeCount?: number }; liked?: boolean; likeCount?: number }>(`/posts/${postId}/like`);
      return unwrap(response.data) ?? {};
    }
  },
  async getComments(postId: number, page = 0, size = 20): Promise<PageResult<Comment>> {
    const response = await api.get<PageResult<Comment> | { data: PageResult<Comment> }>(`/posts/${postId}/comments`, { params: { page, size } });
    return unwrap(response.data);
  },
  async comment(postId: number, content: string): Promise<Comment> {
    const response = await api.post<Comment | { data: Comment }>(`/posts/${postId}/comments`, { content });
    return unwrap(response.data);
  },
  async remove(postId: number): Promise<void> {
    await api.delete(`/posts/${postId}`);
  },
};
