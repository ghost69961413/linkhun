export type RepositoryVisibility = 'PUBLIC' | 'PRIVATE';

export interface LinkHubRepository {
  id: number;
  name: string;
  description?: string;
  repositoryUrl?: string | null;
  visibility: RepositoryVisibility;
  ownerId: number;
  ownerName?: string;
  ownerUsername: string;
  initializeReadme: boolean;
  license?: string | null;
  gitignoreTemplate?: string | null;
  defaultBranch: string;
  filesCount: number;
  stars: number;
  forks: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface RepositoryFile {
  id: number;
  path: string;
  directory: boolean;
  binary?: boolean;
  mimeType?: string | null;
  size: number;
  updatedAt?: string;
  content?: string | null;
}

export interface RepositoryCommit {
  id: number;
  message: string;
  changeSummary?: string;
  branch: string;
  author: string;
  createdAt: string;
}

export interface RepositoryCreateInput {
  name: string;
  description: string;
  visibility: RepositoryVisibility;
  initializeReadme: boolean;
  license: string;
  gitignoreTemplate: string;
}
