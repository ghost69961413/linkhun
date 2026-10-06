import api from './api';
import type { LinkHubRepository, RepositoryCommit, RepositoryCreateInput, RepositoryFile } from '../types/repository';

function unwrap<T>(value: T | { data: T }): T {
  return value && typeof value === 'object' && 'data' in value ? (value as { data: T }).data : value as T;
}

export const repositoryService = {
  async listPublic(): Promise<LinkHubRepository[]> {
    const response = await api.get<{ data: LinkHubRepository[] } | LinkHubRepository[]>('/repositories');
    return unwrap(response.data) ?? [];
  },
  async listMine(): Promise<LinkHubRepository[]> {
    const response = await api.get<{ data: LinkHubRepository[] } | LinkHubRepository[]>('/repositories/me');
    return unwrap(response.data) ?? [];
  },
  async listUser(userId: number): Promise<LinkHubRepository[]> {
    const response = await api.get<{ data: LinkHubRepository[] } | LinkHubRepository[]>(`/repositories/user/${userId}`);
    return unwrap(response.data) ?? [];
  },
  async create(input: RepositoryCreateInput): Promise<LinkHubRepository> {
    const response = await api.post<{ data: LinkHubRepository } | LinkHubRepository>('/repositories', input);
    return unwrap(response.data);
  },
  async get(owner: string, name: string): Promise<LinkHubRepository> {
    const response = await api.get<{ data: LinkHubRepository } | LinkHubRepository>(`/repositories/${encodeURIComponent(owner)}/${encodeURIComponent(name)}`);
    return unwrap(response.data);
  },
  async files(id: number, branch: string): Promise<RepositoryFile[]> {
    const response = await api.get<{ data: RepositoryFile[] } | RepositoryFile[]>(`/repositories/${id}/files`, { params: { branch } });
    return unwrap(response.data) ?? [];
  },
  async file(id: number, fileId: number): Promise<RepositoryFile> {
    const response = await api.get<{ data: RepositoryFile } | RepositoryFile>(`/repositories/${id}/files/${fileId}`);
    return unwrap(response.data);
  },
  async download(id: number, fileId: number): Promise<Blob> {
    const response = await api.get<Blob>(`/repositories/${id}/files/${fileId}/download`, { responseType: 'blob' });
    return response.data;
  },
  async createFile(id: number, branch: string, path: string, content: string, directory = false): Promise<RepositoryFile> {
    const response = await api.post<{ data: RepositoryFile } | RepositoryFile>(`/repositories/${id}/files`, { path, content, directory }, { params: { branch } });
    return unwrap(response.data);
  },
  async upload(id: number, branch: string, file: File, path: string): Promise<RepositoryFile> {
    const body = new FormData(); body.append('file', file); body.append('path', path); body.append('branch', branch);
    const response = await api.post<{ data: RepositoryFile } | RepositoryFile>(`/repositories/${id}/files/upload`, body, { headers: { 'Content-Type': undefined } });
    return unwrap(response.data);
  },
  async updateFile(id: number, fileId: number, content: string): Promise<RepositoryFile> {
    const response = await api.put<{ data: RepositoryFile } | RepositoryFile>(`/repositories/${id}/files/${fileId}`, { content, commitMessage: `Update file` });
    return unwrap(response.data);
  },
  async removeFile(id: number, fileId: number): Promise<void> { await api.delete(`/repositories/${id}/files/${fileId}`); },
  async branches(id: number): Promise<string[]> {
    const response = await api.get<{ data: string[] } | string[]>(`/repositories/${id}/branches`); return unwrap(response.data) ?? [];
  },
  async createBranch(id: number, name: string, fromBranch: string): Promise<string> {
    const response = await api.post<{ data: string } | string>(`/repositories/${id}/branches`, { name, fromBranch }); return unwrap(response.data);
  },
  async commits(id: number, branch: string): Promise<RepositoryCommit[]> {
    const response = await api.get<{ data: RepositoryCommit[] } | RepositoryCommit[]>(`/repositories/${id}/commits`, { params: { branch } }); return unwrap(response.data) ?? [];
  },
  async remove(id: number): Promise<void> { await api.delete(`/repositories/${id}`); },
};
