import axios from 'axios';

const api = axios.create({
  // Vite variables are compiled into the client; set VITE_API_URL in the
  // deployment environment to the public backend API root (including /api).
  baseURL: import.meta.env.VITE_API_URL ?? import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 20_000,
  headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('linkhub.accessToken') ?? sessionStorage.getItem('linkhub.accessToken');
  if (token && token !== 'demo-session') config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('linkhub.accessToken');
      localStorage.removeItem('linkhub.user');
      localStorage.removeItem('linkhub.authUser');
      sessionStorage.removeItem('linkhub.accessToken');
      sessionStorage.removeItem('linkhub.authUser');
      window.dispatchEvent(new CustomEvent('linkhub:unauthorized'));
    }
    return Promise.reject(error);
  },
);

export default api;
