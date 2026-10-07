import { create } from 'zustand';
import type { AuthSession, AuthUser } from '../../types/auth';
import { queryClient } from '../../services/queryClient';

const TOKEN_KEY = 'linkhub.accessToken';
const USER_KEY = 'linkhub.authUser';
const DEMO_KEY = 'linkhub.isDemo';

interface AuthState {
  user: AuthUser | null;
  accessToken: string | null;
  isDemo: boolean;
  rememberMe: boolean;
  setSession: (session: AuthSession) => void;
  updateUser: (user: AuthUser) => void;
  enterDemo: () => void;
  signOut: () => void;
}

const demoUser: AuthUser = { id: 1, username: 'sarahbuilds', email: 'sarah@linkhub.dev', firstName: 'Sarah', lastName: 'Chen', role: 'USER' };

function readInitialState() {
  if (typeof window === 'undefined') return { user: null, accessToken: null, isDemo: false, rememberMe: true };
  const localToken = localStorage.getItem(TOKEN_KEY);
  const sessionToken = sessionStorage.getItem(TOKEN_KEY);
  const accessToken = localToken ?? sessionToken;
  const persistent = Boolean(localToken);
  const storage = persistent ? localStorage : sessionStorage;
  let user: AuthUser | null = null;
  try {
    const raw = storage.getItem(USER_KEY);
    user = raw ? JSON.parse(raw) as AuthUser : null;
  } catch {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    sessionStorage.removeItem(USER_KEY);
  }
  const isDemo = storage.getItem(DEMO_KEY) === 'true' && import.meta.env.DEV;
  if (accessToken === 'demo-session' && !isDemo) {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    return { user: null, accessToken: null, isDemo: false, rememberMe: true };
  }
  return { user, accessToken, isDemo, rememberMe: persistent };
}

function clearSavedSession() {
  for (const storage of [localStorage, sessionStorage]) {
    storage.removeItem(TOKEN_KEY);
    storage.removeItem(USER_KEY);
    storage.removeItem(DEMO_KEY);
  }
}

export const useAuthStore = create<AuthState>((set) => ({
  ...readInitialState(),
  setSession: (session) => {
    queryClient.clear();
    clearSavedSession();
    const storage = session.rememberMe ? localStorage : sessionStorage;
    storage.setItem(TOKEN_KEY, session.accessToken);
    storage.setItem(USER_KEY, JSON.stringify(session.user));
    set({ user: session.user, accessToken: session.accessToken, isDemo: false, rememberMe: session.rememberMe });
  },
  updateUser: (user) => {
    const storage = localStorage.getItem(TOKEN_KEY) ? localStorage : sessionStorage;
    storage.setItem(USER_KEY, JSON.stringify(user));
    set({ user });
  },
  enterDemo: () => {
    if (!import.meta.env.DEV) return;
    queryClient.clear();
    clearSavedSession();
    localStorage.setItem(TOKEN_KEY, 'demo-session');
    localStorage.setItem(USER_KEY, JSON.stringify(demoUser));
    localStorage.setItem(DEMO_KEY, 'true');
    set({ user: demoUser, accessToken: 'demo-session', isDemo: true, rememberMe: true });
  },
  signOut: () => {
    queryClient.clear();
    clearSavedSession();
    set({ user: null, accessToken: null, isDemo: false, rememberMe: true });
  },
}));
