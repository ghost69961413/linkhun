import { useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { Bell, BriefcaseBusiness, Check, CheckCheck, ChevronRight, Code2, MessageCircle, RefreshCw, UserRound, Users } from 'lucide-react';
import api from '../services/api';
import { Button } from '../components/ui/Button';
import { getApiErrorMessage } from '../utils/apiError';
import '../notifications.css';

type Notification = { id: number; type: string; message: string; senderId?: number; senderUsername?: string; referenceId?: number; isRead: boolean; createdAt: string };
type Envelope<T> = { data: T; success?: boolean } | T;
type Page<T> = { content?: T[]; totalElements?: number } | T[];
const keys = { list: ['notifications'] as const, unread: ['notifications', 'unread-count'] as const };
function unwrap<T>(response: Envelope<T>): T { return response && typeof response === 'object' && 'data' in response ? response.data : response as T; }
function listOf<T>(response: Envelope<Page<T>>): T[] { const page = unwrap(response); return Array.isArray(page) ? page : page?.content ?? []; }
function bucket(dateText: string) { const date = new Date(dateText); if (Number.isNaN(date.getTime())) return 'Earlier'; const start = new Date(); start.setHours(0, 0, 0, 0); const days = Math.floor((start.getTime() - new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime()) / 86_400_000); return days <= 0 ? 'Today' : days === 1 ? 'Yesterday' : days < 7 ? 'This week' : 'Earlier'; }
function timeLabel(dateText: string) { const date = new Date(dateText); if (Number.isNaN(date.getTime())) return ''; return new Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(date); }
function notificationIcon(type: string) { const value = type.toLowerCase(); if (value.includes('connect') || value.includes('follow')) return Users; if (value.includes('message') || value.includes('chat')) return MessageCircle; if (value.includes('job') || value.includes('application')) return BriefcaseBusiness; if (value.includes('project') || value.includes('star')) return Code2; return Bell; }
function destination(notification: Notification) { const type = notification.type.toLowerCase(); if (type.includes('connect') || type.includes('follow')) return '/network'; if (type.includes('message') || type.includes('chat')) return '/messages'; if (type.includes('job') || type.includes('application')) return '/jobs'; if (type.includes('project')) return notification.referenceId ? `/projects/${notification.referenceId}` : '/projects'; if (type.includes('post') || type.includes('comment') || type.includes('like')) return '/home'; if (type.includes('profile') && notification.senderUsername) return `/profile/${encodeURIComponent(notification.senderUsername)}`; return notification.senderUsername ? `/profile/${encodeURIComponent(notification.senderUsername)}` : '/notifications'; }

export function NotificationsPage() {
  const [filter, setFilter] = useState<'all' | 'unread'>('all'); const [actionError, setActionError] = useState(''); const [notice, setNotice] = useState(''); const navigate = useNavigate(); const client = useQueryClient();
  const notificationsQuery = useQuery({ queryKey: keys.list, queryFn: async () => { const { data } = await api.get<Envelope<Page<Notification>>>('/notifications', { params: { page: 0, size: 100 } }); return listOf(data); }, staleTime: 20_000, refetchInterval: 60_000 });
  const unreadQuery = useQuery({ queryKey: keys.unread, queryFn: async () => { const { data } = await api.get<Envelope<number>>('/notifications/unread-count'); return unwrap(data); }, staleTime: 20_000, refetchInterval: 60_000 });
  const refresh = async () => { await Promise.all([client.invalidateQueries({ queryKey: keys.list }), client.invalidateQueries({ queryKey: keys.unread })]); };
  const markRead = useMutation({ mutationFn: (id: number) => api.put(`/notifications/${id}/read`), onSuccess: refresh });
  const markAllRead = useMutation({ mutationFn: () => api.put('/notifications/read-all'), onSuccess: refresh });
  const notifications = notificationsQuery.data ?? [];
  const unreadCount = unreadQuery.data ?? notifications.filter(item => !item.isRead).length;
  const grouped = useMemo(() => {
    const items = notifications.filter(item => filter === 'all' || !item.isRead);
    const names = ['Today', 'Yesterday', 'This week', 'Earlier'];
    return names.map(name => ({ name, items: items.filter(item => bucket(item.createdAt) === name) })).filter(group => group.items.length > 0);
  }, [notifications, filter]);
  const fail = (error: unknown) => setActionError(getApiErrorMessage(error, 'Could not update notifications.'));
  const open = (item: Notification) => { setActionError(''); if (!item.isRead) markRead.mutate(item.id, { onError: fail }); navigate(destination(item)); };
  const runMarkAll = () => { setActionError(''); markAllRead.mutate(undefined, { onError: fail, onSuccess: () => { setNotice('All caught up.'); window.setTimeout(() => setNotice(''), 3000); } }); };
  const initialError = notificationsQuery.error || unreadQuery.error;
  return <div className="workspace-page notifications-page">
    <div className="notifications-heading"><div><div className="page-kicker"><Bell size={13}/> A LITTLE SOMETHING</div><h1>Notifications</h1><p>Updates from people and projects in your orbit.</p></div><div className="notification-heading-actions"><span className="unread-counter"><i/>{unreadCount} unread</span><Button disabled={!unreadCount || markAllRead.isPending} onClick={runMarkAll}>{markAllRead.isPending ? <RefreshCw className="notification-spin" size={14}/> : <CheckCheck size={14}/>} Mark all read</Button></div></div>
    {actionError && <div className="notification-alert" role="alert">{actionError}<button onClick={() => setActionError('')}>Dismiss</button></div>}{notice && <div className="notification-success" role="status"><Check size={14}/>{notice}</div>}
    <section className="notifications-panel"><div className="notifications-toolbar"><div className="notification-filters" role="tablist" aria-label="Notification filter"><button role="tab" aria-selected={filter === 'all'} className={filter === 'all' ? 'active' : ''} onClick={() => setFilter('all')}>All <span>{notifications.length}</span></button><button role="tab" aria-selected={filter === 'unread'} className={filter === 'unread' ? 'active' : ''} onClick={() => setFilter('unread')}>Unread <span>{unreadCount}</span></button></div><span className="notifications-sort-label">Most recent first</span></div>
      {notificationsQuery.isLoading ? <div className="notification-skeleton">{[1, 2, 3, 4].map(item => <i key={item}/>)}</div> : notificationsQuery.isError ? <div className="notification-empty"><span><Bell size={20}/></span><h2>Notifications are unavailable</h2><p>{getApiErrorMessage(initialError, 'Could not load your notifications.')}</p><Button onClick={() => { void notificationsQuery.refetch(); void unreadQuery.refetch(); }}><RefreshCw size={14}/> Try again</Button></div> : grouped.length === 0 ? <div className="notification-empty"><span><CheckCheck size={20}/></span><h2>{filter === 'unread' ? 'You’re all caught up.' : 'No notifications yet.'}</h2><p>{filter === 'unread' ? 'New updates will appear here when they arrive.' : 'When something happens in your network, you’ll see it here.'}</p>{filter === 'unread' && <Button onClick={() => setFilter('all')}>View all notifications</Button>}</div> : <div className="notification-groups">{grouped.map(group => <section key={group.name} className="notification-group"><h2>{group.name}<span>{group.items.length}</span></h2><div className="notification-group-items">{group.items.map(item => { const Icon = notificationIcon(item.type); return <article key={item.id} className={`notification-item ${item.isRead ? 'read' : 'unread'}`}><button className="notification-main" onClick={() => open(item)}><span className={`notification-icon type-${item.type.toLowerCase().replace(/[^a-z0-9]/g, '-')}`}><Icon size={16}/></span><span className="notification-copy"><span className="notification-message">{item.senderUsername && <b>@{item.senderUsername} </b>}{item.message}</span><span className="notification-meta"><span>{item.type.replace(/_/g, ' ')}</span><i>·</i><time>{timeLabel(item.createdAt)}</time></span></span><ChevronRight size={15} className="notification-chevron"/></button>{!item.isRead && <><span className="unread-dot" aria-label="Unread"/><button className="mark-read-button" disabled={markRead.isPending} aria-label="Mark notification as read" title="Mark as read" onClick={() => markRead.mutate(item.id, { onError: fail })}><Check size={14}/></button></>}</article>; })}</div></section>)}</div>}
      <div className="notifications-footer">Showing the latest {notifications.length} {notifications.length === 1 ? 'notification' : 'notifications'} from your account.</div>
    </section>
  </div>;
}
