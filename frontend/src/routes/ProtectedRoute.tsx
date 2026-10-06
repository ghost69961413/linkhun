import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuthStore } from '../features/auth/authStore';
import { isJwtExpired } from '../utils/jwt';

export function ProtectedRoute() {
  const token = useAuthStore((state) => state.accessToken);
  const isDemo = useAuthStore((state) => state.isDemo);
  const location = useLocation();
  const validSession = Boolean(token) && (isDemo || !isJwtExpired(token!));
  return validSession ? <Outlet /> : <Navigate to="/login" replace state={{ from: location }} />;
}
