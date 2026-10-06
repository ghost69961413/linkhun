import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../features/auth/authStore';
export function AdminRoute(){const role=useAuthStore(s=>s.user?.role);return role==='ADMIN'?<Outlet/>:<Navigate to="/home" replace/>}
