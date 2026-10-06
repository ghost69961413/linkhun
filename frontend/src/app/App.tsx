import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from '../routes/ProtectedRoute';
import { AdminRoute } from '../routes/AdminRoute';
import { AppLayout } from '../layouts/AppLayout';
import { PageLoadingSkeleton } from '../components/ui/Skeleton';
const ProfilePage = lazy(() => import('../pages/ProfilePage').then(module => ({ default: module.ProfilePage })));
const ProjectsPage = lazy(() => import('../pages/ProjectsPage').then(module => ({ default: module.ProjectsPage })));
const RepositoriesPage = lazy(() => import('../pages/RepositoriesPage').then(module => ({ default: module.RepositoriesPage })));
const LoginPage = lazy(() => import('../pages/LoginPage').then(module => ({ default: module.LoginPage })));
const RegisterPage = lazy(() => import('../pages/RegisterPage').then(module => ({ default: module.RegisterPage })));
const HomeFeed = lazy(() => import('../pages/home/HomeFeed').then(module => ({ default: module.HomeFeed })));
const NetworkPage = lazy(() => import('../pages/NetworkPage').then(module => ({ default: module.NetworkPage })));
const GlobalSearchPage = lazy(() => import('../pages/GlobalSearchPage').then(module => ({ default: module.GlobalSearchPage })));
const JobsPage = lazy(() => import('../pages/JobsPage').then(module => ({ default: module.JobsPage })));
const MessagesPage = lazy(() => import('../pages/MessagesPage').then(module => ({ default: module.MessagesPage })));
const NotificationsPage = lazy(() => import('../pages/NotificationsPage').then(module => ({ default: module.NotificationsPage })));
const WorkspacePage = lazy(() => import('../pages/WorkspacePage').then(module => ({ default: module.WorkspacePage })));
export default function App(){return <Suspense fallback={<div className="workspace-content"><PageLoadingSkeleton/></div>}><Routes>
 <Route path="/login" element={<LoginPage/>}/><Route path="/register" element={<RegisterPage/>}/>
 <Route element={<ProtectedRoute/>}><Route element={<AppLayout/>}>
  <Route index element={<Navigate to="/home" replace/>}/><Route path="/home" element={<HomeFeed/>}/><Route path="/profile/create" element={<ProfilePage mode="create"/>}/><Route path="/profile/edit" element={<ProfilePage mode="edit"/>}/><Route path="/profile/me" element={<ProfilePage mode="view"/>}/><Route path="/profile/:id" element={<ProfilePage mode="view"/>}/><Route path="/profile" element={<ProfilePage mode="view"/>}/><Route path="/network" element={<NetworkPage/>}/><Route path="/network/pending" element={<NetworkPage/>}/><Route path="/network/sent" element={<NetworkPage/>}/><Route path="/network/connections" element={<NetworkPage/>}/><Route path="/jobs" element={<JobsPage/>}/><Route path="/jobs/my-applications" element={<JobsPage/>}/><Route path="/jobs/recruiter" element={<JobsPage/>}/><Route path="/jobs/:id" element={<JobsPage/>}/><Route path="/projects" element={<ProjectsPage mode="list"/>}/><Route path="/projects/create" element={<ProjectsPage mode="create"/>}/><Route path="/projects/edit/:id" element={<ProjectsPage mode="edit"/>}/><Route path="/projects/:id" element={<ProjectsPage mode="detail"/>}/><Route path="/repositories" element={<RepositoriesPage mode="list"/>}/><Route path="/repositories/create" element={<RepositoriesPage mode="create"/>}/><Route path="/repositories/:owner/:repo" element={<RepositoriesPage mode="detail"/>}/><Route path="/messages" element={<MessagesPage/>}/><Route path="/notifications" element={<NotificationsPage/>}/><Route path="/search" element={<GlobalSearchPage/>}/><Route path="/settings" element={<WorkspacePage page="Settings"/>}/><Route element={<AdminRoute/>}><Route path="/admin" element={<WorkspacePage page="Admin"/>}/></Route>
 </Route></Route><Route path="*" element={<Navigate to="/home" replace/>}/></Routes></Suspense>}
