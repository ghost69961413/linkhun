import api from './api';
import type { AuthApiResponse } from '../types/auth';

export interface ChangeEmailInput { newEmail: string; password: string }

export const userService = {
  async changeEmail(input: ChangeEmailInput) {
    const { data } = await api.put<AuthApiResponse>('/users/change-email', input);
    return data;
  },
};
