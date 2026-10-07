import api from './api';
import type { AuthApiResponse, AuthSession, LoginInput, RegisterInput } from '../types/auth';
import { splitFullName } from '../utils/authValidation';

function toSession(result: AuthApiResponse, rememberMe: boolean, fullName?: string): AuthSession {
  if (!result?.token || !result.email || !result.username) {
    throw new Error('The server returned an incomplete sign-in response.');
  }

  const [firstName = result.username, ...lastNameParts] = fullName ? splitFullName(fullName) : [result.username];
  return {
    accessToken: result.token,
    rememberMe,
    user: {
      username: result.username,
      email: result.email,
      firstName,
      lastName: lastNameParts.join(' '),
      role: 'USER',
    },
  };
}

export const authService = {
  async login(input: LoginInput): Promise<AuthSession> {
    const { data } = await api.post<AuthApiResponse>('/auth/login', {
      usernameOrEmail: input.usernameOrEmail.trim(),
      password: input.password,
    }, {
      // Render's free instance can take several minutes to cold-start. Keep
      // the login request alive long enough for Spring Boot to finish starting.
      timeout: 270_000,
    });
    return toSession(data, input.rememberMe);
  },

  async register(input: RegisterInput): Promise<AuthSession> {
    const [firstName, ...lastNameParts] = splitFullName(input.fullName);
    const { data } = await api.post<AuthApiResponse>('/auth/register', {
      firstName,
      lastName: lastNameParts.join(' '),
      username: input.username.trim(),
      email: input.email.trim().toLowerCase(),
      password: input.password,
      profileType: input.profileType,
    });
    return toSession(data, true, input.fullName);
  },
};
