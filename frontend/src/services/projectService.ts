import api from './api';
import type { Project, ProjectPayload } from '../types/project';

function unwrap<T>(value: T | { data: T }): T {
  return value && typeof value === 'object' && 'data' in value ? (value as { data: T }).data : value as T;
}

export const projectService = {
  async list(userId?: number): Promise<Project[]> {
    if (userId != null) {
      const response = await api.get<Project[] | { data: Project[] }>(`/projects/user/${userId}`);
      return unwrap(response.data) ?? [];
    }
    const response = await api.get<Project[] | { data: Project[] }>('/projects/me');
    return unwrap(response.data) ?? [];
  },
  async get(id: string): Promise<Project> {
    const response = await api.get<Project | { data: Project }>(`/projects/${encodeURIComponent(id)}`);
    return unwrap(response.data);
  },
  async create(payload: ProjectPayload): Promise<Project> {
    const body = new FormData();
    body.append('title', payload.title);
    body.append('description', payload.description);
    body.append('visibility', payload.visibility);
    body.append('githubUrl', payload.githubUrl);
    if (payload.linkhubRepositoryId) body.append('linkhubRepositoryId', String(payload.linkhubRepositoryId));
    body.append('liveDemoUrl', payload.liveDemoUrl);
    payload.technologies.forEach(value => body.append('technologies', value));
    payload.features.forEach(value => body.append('features', value));
    payload.teamMembers.forEach(value => body.append('teamMembers', value));
    payload.screenshots.forEach((file, index) => body.append(index === 0 ? 'thumbnail' : 'screenshots', file));
    const response = await api.post<Project | { data: Project }>('/projects', body, { headers: { 'Content-Type': 'multipart/form-data' } });
    return unwrap(response.data);
  },
  async update(id: string, payload: Omit<ProjectPayload, 'screenshots'>): Promise<Project> {
    const response = await api.put<Project | { data: Project }>(`/projects/${encodeURIComponent(id)}`, {
      title: payload.title,
      description: payload.description,
      githubUrl: payload.githubUrl,
      linkhubRepositoryId: payload.linkhubRepositoryId || null,
      liveDemoUrl: payload.liveDemoUrl,
      technologies: payload.technologies,
      features: payload.features,
      teamMembers: payload.teamMembers,
      visibility: payload.visibility,
    });
    return unwrap(response.data);
  },
  async remove(id: string): Promise<void> {
    await api.delete(`/projects/${encodeURIComponent(id)}`);
  },
};
