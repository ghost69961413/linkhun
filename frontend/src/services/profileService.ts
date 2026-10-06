import api from './api';
import type { Profile, ProfilePayload } from '../types/profile';

function unwrap<T>(response: T | { data: T }): T {
  return response && typeof response === 'object' && 'data' in response
    ? (response as { data: T }).data
    : response as T;
}

function normalize(raw: Partial<Profile> & { firstName?: string; lastName?: string }): Profile {
  return {
    id: raw.id,
    userId: raw.userId,
    username: raw.username ?? '',
    fullName: raw.fullName ?? [raw.firstName, raw.lastName].filter(Boolean).join(' '),
    firstName: raw.firstName,
    lastName: raw.lastName,
    headline: raw.headline ?? '',
    location: raw.location ?? '',
    bio: raw.bio ?? '',
    profilePicture: raw.profilePicture ?? '',
    coverImage: raw.coverImage ?? '',
    skills: raw.skills ?? [],
    experience: raw.experience ?? [],
    education: raw.education ?? [],
    certifications: raw.certifications ?? [],
    github: raw.github ?? raw.githubUrl ?? '',
    linkedin: raw.linkedin ?? raw.linkedinUrl ?? '',
    portfolio: raw.portfolio ?? raw.portfolioUrl ?? raw.website ?? '',
    website: raw.website,
    githubUrl: raw.githubUrl,
    linkedinUrl: raw.linkedinUrl,
    portfolioUrl: raw.portfolioUrl,
    profileType: raw.profileType ?? 'PROFESSIONAL',
    roleDetails: raw.roleDetails ?? {},
  };
}

export const profileService = {
  async getMine(): Promise<Profile> {
    const response = await api.get<Profile | { data: Profile }>('/profile/me');
    return normalize(unwrap(response.data));
  },
  async saveMine(profile: ProfilePayload): Promise<Profile> {
    const response = await api.put<Profile | { data: Profile }>('/profile/me', {
      username: profile.username,
      fullName: profile.fullName,
      headline: profile.headline,
      bio: profile.bio,
      location: profile.location,
      coverImage: profile.coverImage,
      skills: profile.skills,
      experience: profile.experience,
      education: profile.education,
      certifications: profile.certifications,
      website: profile.portfolio,
      githubUrl: profile.github,
      linkedinUrl: profile.linkedin,
      portfolioUrl: profile.portfolio,
      profileType: profile.profileType,
      roleDetails: profile.roleDetails,
    });
    return normalize(unwrap(response.data));
  },
  async getById(id: string): Promise<Profile> {
    const response = await api.get<Profile | { data: Profile }>(`/profile/${encodeURIComponent(id)}`);
    return normalize(unwrap(response.data));
  },
  async uploadPicture(file: File, kind: 'picture' | 'cover' = 'picture'): Promise<string> {
    const form = new FormData();
    form.append('image', file);
    // The shared Axios client defaults to application/json. Without clearing
    // that header Axios serializes FormData as JSON, so Spring receives no file.
    // Leave Content-Type unset here so the browser writes multipart/form-data
    // with its required boundary.
    const response = await api.post<{ data?: string; url?: string }>(`/profile/${kind}`, form, {
      headers: { 'Content-Type': undefined },
    });
    const url = response.data.data ?? response.data.url ?? '';
    if (!url) throw new Error('The server did not return the uploaded image URL.');
    return url;
  },
};
