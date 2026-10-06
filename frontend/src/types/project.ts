export interface ProjectOwner {
  id?: number;
  userId?: number;
  username?: string;
  fullName?: string;
  name?: string;
  profilePicture?: string | null;
}

export interface Project {
  id: number;
  title: string;
  description: string;
  githubUrl: string;
  linkhubRepositoryId?: number | null;
  linkhubRepositoryPath?: string | null;
  liveDemoUrl: string;
  thumbnailUrl?: string | null;
  screenshots?: string[];
  technologies: string[];
  features?: string[];
  teamMembers?: string[];
  owner?: ProjectOwner | null;
  ownerName?: string;
  stars?: number | null;
  views?: number | null;
  createdAt?: string;
  updatedAt?: string;
  visibility?: 'PUBLIC' | 'PRIVATE';
}

export interface ProjectPayload {
  title: string;
  description: string;
  githubUrl: string;
  linkhubRepositoryId?: number | '';
  liveDemoUrl: string;
  technologies: string[];
  screenshots: File[];
  features: string[];
  teamMembers: string[];
  visibility: 'PUBLIC' | 'PRIVATE';
}
