export interface AuthUser {
  id?: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  profilePicture?: string | null;
  role?: 'USER' | 'ADMIN';
}

export interface LoginInput {
  usernameOrEmail: string;
  password: string;
  rememberMe: boolean;
}

export interface RegisterInput {
  fullName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  profileType: import('./profile').ProfileType;
}

export interface AuthApiResponse {
  token: string;
  message: string;
  email: string;
  username: string;
}

export interface AuthSession {
  accessToken: string;
  user: AuthUser;
  rememberMe: boolean;
}

export interface ApiEnvelope<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}
