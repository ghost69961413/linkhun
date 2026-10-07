import axios from 'axios';

export function getApiErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const body = error.response?.data as { message?: unknown; error?: unknown; errors?: unknown } | undefined;
    if (import.meta.env.DEV) {
      console.error('[LinkHub API response]', {
        method: error.config?.method?.toUpperCase(),
        url: error.config?.url,
        status: error.response?.status,
        data: error.response?.data,
        code: error.code,
        message: error.message,
      });
    }
    if (error.response?.status === 401) return 'Please log in again.';
    if (error.response?.status === 403) return 'You do not have permission to do that.';
    if (error.response?.status === 429) return 'LinkHub is busy right now. Please wait a moment and try again.';
    if (error.response?.status === 503) return 'The LinkHub API is starting or temporarily unavailable. Please try again shortly.';
    if ([502, 504].includes(error.response?.status ?? 0)) return 'The LinkHub API is temporarily unavailable. Please try again shortly.';
    if (error.response?.status === 409) return typeof body?.message === 'string' ? body.message : 'That email or username is already registered.';
    if (error.response?.status && error.response.status >= 500) return 'Server error. Please try again.';
    if (typeof body?.message === 'string' && body.message.trim()) return body.message;
    if (typeof body?.error === 'string' && body.error.trim()) return body.error;
    if (typeof body?.errors === 'string' && body.errors.trim()) return body.errors;
    if (error.response?.status === 400 || error.response?.status === 422) {
      return 'Some submitted information was rejected. Review the fields and try again.';
    }
    if (error.response?.status === 404) return 'The requested LinkHub resource was not found.';
    if (!error.response && error.code === 'ECONNABORTED') return 'The LinkHub API request timed out. Check that the backend and database are responding.';
    if (!error.response && (error.code === 'ERR_NETWORK' || error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND')) {
      return 'The LinkHub API could not be reached. Check the API URL and confirm the backend allows this app’s origin.';
    }
  }
  if (error instanceof Error && error.message) return error.message;
  return fallback;
}
