import { useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom';
import { Check, Clock3, Search, Sparkles, Users, UserRoundPlus, UserRoundX, X } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { useAuthStore } from '../features/auth/authStore';
import { useAcceptedConnections, useConnectionAction, useConnectionSuggestions, useMutualConnections, usePendingConnections, useSendConnection, useSentConnections } from '../hooks/useConnections';
import { getApiErrorMessage } from '../utils/apiError';
import type { ConnectionRequest, ConnectionUser } from '../types';
import '../network.css';

type NetworkTab = 'suggestions' | 'pending' | 'sent' | 'connections';
const tabs: { path: string; tab: NetworkTab; label: string }[] = [
  { path: '/network', tab: 'suggestions', label: 'Suggestions' },
  { path: '/network/pending', tab: 'pending', label: 'Received' },
  { path: '/network/sent', tab: 'sent', label: 'Sent' },
  { path: '/network/connections', tab: 'connections', label: 'Connections' },
];

function requestOther(request: ConnectionRequest, tab: NetworkTab, userId?: number): ConnectionUser {
  if (tab === 'pending') return request.sender;
  if (tab === 'sent') return request.recipient;
  return request.sender.userId === userId ? request.recipient : request.sender;
}

export function NetworkPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const me = useAuthStore(state => state.user);
  const [search, setSearch] = useState('');
  const [actionError, setActionError] = useState('');
  const tab: NetworkTab = location.pathname.endsWith('/pending') ? 'pending' : location.pathname.endsWith('/sent') ? 'sent' : location.pathname.endsWith('/connections') ? 'connections' : 'suggestions';
  const pending = usePendingConnections();
  const sent = useSentConnections();
  const connections = useAcceptedConnections();
  const suggestionQuery = useConnectionSuggestions(search.trim(), tab === 'suggestions');
  const send = useSendConnection();
  const accept = useConnectionAction('accept');
  const reject = useConnectionAction('reject');
  const withdraw = useConnectionAction('withdraw');

  const knownIds = useMemo(() => new Set([
    ...(pending.data ?? []).flatMap(item => [item.sender.userId, item.recipient.userId]),
    ...(sent.data ?? []).flatMap(item => [item.sender.userId, item.recipient.userId]),
    ...(connections.data ?? []).flatMap(item => [item.sender.userId, item.recipient.userId]),
  ].filter((value): value is number => Number.isFinite(value))), [pending.data, sent.data, connections.data]);
  const suggestions = (suggestionQuery.data ?? []).filter(person => person.userId && person.userId !== me?.id && person.username?.toLowerCase() !== me?.username?.toLowerCase() && !knownIds.has(person.userId));
  const receivedRows = pending.data ?? [];
  const sentRows = sent.data ?? [];
  const connectionRows = connections.data ?? [];
  const currentList = tab === 'pending' ? pending : tab === 'sent' ? sent : connections;
  const activeError = tab === 'suggestions' ? suggestionQuery.error : currentList.error;
  const activeLoading = tab === 'suggestions' ? suggestionQuery.isLoading : currentList.isLoading;
  const title: Record<NetworkTab, string> = { suggestions: 'Suggested connections', pending: 'Connection requests', sent: 'Invitations you sent', connections: 'Your connections' };
  const description: Record<NetworkTab, string> = { suggestions: 'Discover LinkHub members and start a conversation.', pending: 'Decide who you would like to connect with.', sent: 'Follow the requests waiting for a reply.', connections: 'People you have connected with on LinkHub.' };
  const count = tab === 'pending' ? receivedRows.length : tab === 'sent' ? sentRows.length : tab === 'connections' ? connectionRows.length : suggestions.length;

  function runAction(action: () => void) { setActionError(''); action(); }

  return <div className="workspace-page network-page"><div className="page-heading-row network-heading"><div><div className="page-kicker"><Users size={13}/> YOUR NETWORK</div><h1>{title[tab]}</h1><p>{description[tab]}</p></div>{tab === 'suggestions' && <div className="network-members-count"><Users size={14}/>{suggestions.length} people to explore</div>}</div>
    <nav className="network-route-tabs" aria-label="Network sections">{tabs.map(item => <NavLink end={item.path === '/network'} key={item.path} to={item.path} className={({ isActive }) => isActive ? 'active' : ''}>{item.label}{item.tab === 'pending' && receivedRows.length > 0 && <span>{receivedRows.length}</span>}</NavLink>)}</nav>
    {tab === 'suggestions' && <label className="network-search"><Search size={15}/><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search by name or username" aria-label="Search LinkHub members"/><span>MEMBERS</span></label>}
    {actionError && <div className="network-alert" role="alert">{actionError}<button onClick={() => setActionError('')} aria-label="Dismiss"><X size={13}/></button></div>}
    {activeError && <div className="network-alert" role="alert"><span>{getApiErrorMessage(activeError, 'Could not load this part of your network.')}</span><button onClick={() => void (tab === 'suggestions' ? suggestionQuery.refetch() : currentList.refetch())}>Try again</button></div>}
    {activeLoading ? <NetworkLoading/> : tab === 'suggestions' ? suggestions.length === 0 ? <NetworkEmpty title={search.trim() ? 'No members match that search.' : 'No new people to connect with yet.'} detail={search.trim() ? 'Try another name or username.' : 'Check back as new members join LinkHub.'}/> : <div className="network-card-grid">{suggestions.map(person => <MemberCard key={person.userId} person={person} action={<Button variant="primary" size="sm" disabled={send.isPending} onClick={() => runAction(() => send.mutate(person.userId, { onError: error => setActionError(getApiErrorMessage(error, 'Could not send this request.')) }))}><UserRoundPlus size={14}/>Connect</Button>}/>)}</div>
      : tab === 'pending' ? receivedRows.length === 0 ? <NetworkEmpty title="You’re all caught up." detail="New connection requests will appear here."/> : <div className="network-card-grid">{receivedRows.map(request => <MemberCard key={request.requestId} person={request.sender} meta={<span className="network-time"><Clock3 size={12}/>{formatDate(request.createdAt)}</span>} action={<div className="network-card-actions"><Button variant="primary" size="sm" disabled={accept.isPending} onClick={() => runAction(() => accept.mutate(request.requestId, { onError: error => setActionError(getApiErrorMessage(error, 'Could not accept this request.')) }))}><Check size={14}/>Accept</Button><Button size="sm" disabled={reject.isPending} onClick={() => runAction(() => reject.mutate(request.requestId, { onError: error => setActionError(getApiErrorMessage(error, 'Could not reject this request.')) }))}><X size={14}/>Reject</Button></div>}/>)}</div>
      : tab === 'sent' ? sentRows.length === 0 ? <NetworkEmpty title="No pending invitations." detail="When you invite a LinkHub member, you can follow it here."/> : <div className="network-card-grid">{sentRows.map(request => <MemberCard key={request.requestId} person={request.recipient} meta={<span className="network-time"><Clock3 size={12}/>Sent {formatDate(request.createdAt)}</span>} action={<Button size="sm" disabled={withdraw.isPending} onClick={() => runAction(() => withdraw.mutate(request.requestId, { onError: error => setActionError(getApiErrorMessage(error, 'Could not withdraw this request.')) }))}><UserRoundX size={14}/>Withdraw</Button>}/>)}</div>
      : connectionRows.length === 0 ? <NetworkEmpty title="Your network starts here." detail="Discover members and send a connection request to get started."/> : <div className="network-card-grid">{connectionRows.map(request => { const person = requestOther(request, 'connections', me?.id); return <MemberCard key={request.requestId} person={person} meta={<span className="network-connected"><Check size={12}/>Connected</span>} action={<Button variant="primary" size="sm" onClick={() => navigate(`/messages?userId=${person.userId}`)}><span>Message</span></Button>}/>; })}</div>}
    {tab === 'suggestions' && <section className="network-note"><Sparkles size={15}/><span><b>Find people through shared links.</b> Member discovery uses the LinkHub directory; open mutual connections on a card to see who you both know.</span></section>}
  </div>;
}

function MemberCard({ person, meta, action }: { person: ConnectionUser; meta?: ReactNode; action: ReactNode }) {
  const [showMutual, setShowMutual] = useState(false);
  const mutual = useMutualConnections(person.userId, showMutual);
  const initials = person.fullName.split(/\s+/).slice(0, 2).map(part => part[0]).join('').toUpperCase() || person.username.slice(0, 1).toUpperCase();
  return <article className="surface-card network-member-card"><div className="network-card-cover"><span className="network-cover-orbit"/><Sparkles size={14}/></div><div className="network-member-body"><Link to={`/profile/${person.userId}`} className="network-member-profile"><div className="network-member-top">{person.profileImage ? <img src={person.profileImage} alt={`${person.fullName} profile`} className="network-avatar"/> : <span className="network-avatar initials">{initials}</span>}<div className="network-member-identity"><h2>{person.fullName}</h2><small>@{person.username}</small></div></div></Link>{person.headline && <p className="network-member-headline">{person.headline}</p>}{meta && <div className="network-member-meta">{meta}</div>}<div className="network-mutual"><button onClick={() => setShowMutual(value => !value)}><Users size={13}/>{showMutual ? 'Hide mutual connections' : 'Show mutual connections'}</button>{showMutual && <div className="network-mutual-content">{mutual.isLoading ? <span>Loading shared connections…</span> : mutual.isError ? <span>Could not load mutual connections.</span> : mutual.data?.length ? <><b>{mutual.data.length} mutual {mutual.data.length === 1 ? 'connection' : 'connections'}</b><div>{mutual.data.slice(0, 5).map(user => <Link to={`/profile/${user.userId}`} key={user.userId} title={`${user.fullName} (@${user.username})`} className="network-mutual-avatar">{user.profileImage ? <img src={user.profileImage} alt=""/> : user.fullName.split(/\s+/).map(part => part[0]).slice(0, 2).join('').toUpperCase()}</Link>)}</div></> : <span>No mutual connections yet.</span>}</div>}</div><div className="network-member-action">{action}</div></div></article>;
}

function NetworkLoading() { return <div className="network-card-grid">{[0, 1, 2].map(key => <div className="surface-card network-skeleton" key={key}><i/><span/><span/><span/></div>)}</div>; }
function NetworkEmpty({ title, detail }: { title: string; detail: string }) { return <section className="surface-card network-empty"><span><Users size={20}/></span><h2>{title}</h2><p>{detail}</p></section>; }
function formatDate(value: string) { const date = new Date(value); return Number.isNaN(date.getTime()) ? '' : date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' }); }
