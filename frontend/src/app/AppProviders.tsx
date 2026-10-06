import { useEffect } from 'react';
import { QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { queryClient } from '../services/queryClient';
import { ThemeRoot } from './ThemeRoot';
import { ErrorBoundary } from '../components/ErrorBoundary';
import { useAuthStore } from '../features/auth/authStore';

function AuthLifecycle({ children }: { children: React.ReactNode }) {
  const signOut = useAuthStore((state) => state.signOut);
  useEffect(() => {
    const onUnauthorized = () => signOut();
    window.addEventListener('linkhub:unauthorized', onUnauthorized);
    return () => window.removeEventListener('linkhub:unauthorized', onUnauthorized);
  }, [signOut]);
  return <>{children}</>;
}

export function AppProviders({ children }: { children: React.ReactNode }) {
  return <ErrorBoundary><QueryClientProvider client={queryClient}><BrowserRouter><AuthLifecycle><ThemeRoot>{children}</ThemeRoot></AuthLifecycle></BrowserRouter></QueryClientProvider></ErrorBoundary>;
}
