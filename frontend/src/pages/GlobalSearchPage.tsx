import { useEffect, useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { BriefcaseBusiness, Code2, MapPin, Search, Users, FileText, SlidersHorizontal, ExternalLink, RotateCw } from 'lucide-react';
import api from '../services/api';
import { Button } from '../components/ui/Button';
import { getApiErrorMessage } from '../utils/apiError';
import '../search.css';
import { PROFILE_TYPES, type ProfileType } from '../types/profile';

type Kind = 'people' | 'posts' | 'jobs' | 'projects';
type Result = { kind: Kind; id: number | string; title: string; subtitle?: string; description?: string; image?: string | null; location?: string; skills?: string[]; technologies?: string[]; experience?: string; url?: string; author?: string; username?: string; profileType?: ProfileType };
type PageData<T> = { content?: T[] } | T[];
type Envelope<T> = { data: T; success?: boolean } | T;
const tabs = [{ id: 'all', label: 'All' }, { id: 'people', label: 'People' }, { id: 'posts', label: 'Posts' }, { id: 'jobs', label: 'Jobs' }, { id: 'projects', label: 'Projects' }] as const;
const kindIcon = { people: Users, posts: FileText, jobs: BriefcaseBusiness, projects: Code2 };
function unwrap<T>(value: Envelope<PageData<T>>): T[] {
  const body = value && typeof value === 'object' && 'data' in value ? value.data : value as PageData<T>;
  return Array.isArray(body) ? body : body?.content ?? [];
}
const text = (value: unknown) => typeof value === 'string' ? value : '';
function normalise(kind: Kind, raw: any): Result {
  if (kind === 'people') return { kind, id: raw.id, title: [raw.firstName, raw.lastName].filter(Boolean).join(' ') || raw.username, subtitle: raw.username ? `@${raw.username}` : '', description: raw.headline, image: raw.profilePicture, username: raw.username, profileType: raw.profileType ?? 'PROFESSIONAL' };
  if (kind === 'posts') return { kind, id: raw.id, title: raw.firstName ? `${raw.firstName}${raw.username ? ` · @${raw.username}` : ''}` : 'LinkHub post', subtitle: raw.username ? `@${raw.username}` : '', description: raw.content, image: raw.imageUrl, author: raw.username };
  if (kind === 'projects') return { kind, id: raw.id, title: raw.title || 'Project', subtitle: raw.status, description: raw.description, image: raw.thumbnailUrl, technologies: raw.technologies ?? [], url: raw.liveDemoUrl || raw.githubUrl };
  return { kind, id: raw.id, title: raw.title || 'Open role', subtitle: [raw.companyName, raw.jobType].filter(Boolean).join(' · '), description: raw.description, location: raw.location, skills: text(raw.skills).split(/[,;|]/).map(s => s.trim()).filter(Boolean), author: raw.postedByName };
}
async function searchAll(query: string, location: string, profileType: ProfileType | '') {
  const endpoints: [Kind, string, Record<string, string | number>][] = [
    ['people', '/search/users', { query, page: 0, size: 20, ...(profileType ? { profileType } : {}) }],
    ['posts', '/search/posts', { query, page: 0, size: 20 }],
    ['projects', '/search/projects', { query, page: 0, size: 20 }],
    ['jobs', '/jobs/search', { title: query, ...(location.trim() ? { location: location.trim() } : {}), page: 0, size: 20 }],
  ];
  const outcomes = await Promise.allSettled(endpoints.map(async ([kind, path, params]) => {
    const response = await api.get<Envelope<PageData<any>>>(path, { params });
    return unwrap(response.data).map(row => normalise(kind, row));
  }));
  const results = outcomes.flatMap(outcome => outcome.status === 'fulfilled' ? outcome.value : []);
  const failures = outcomes.filter(outcome => outcome.status === 'rejected') as PromiseRejectedResult[];
  if (failures.length === endpoints.length) throw failures[0].reason;
  return { results, failedKinds: outcomes.flatMap((outcome, i) => outcome.status === 'rejected' ? [endpoints[i][0]] : []) };
}

export function GlobalSearchPage() {
  const [params, setParams] = useSearchParams();
  const navigate = useNavigate();
  const [input, setInput] = useState(params.get('q') ?? '');
  const [query, setQuery] = useState(params.get('q') ?? '');
  const [activeTab, setActiveTab] = useState<(typeof tabs)[number]['id']>((params.get('type') as any) || 'all');
  const [filtersOpen, setFiltersOpen] = useState(true);
  const [skills, setSkills] = useState('');
  const [location, setLocation] = useState('');
  const [experience, setExperience] = useState('');
  const [technology, setTechnology] = useState('');
  const [profileType, setProfileType] = useState<ProfileType | ''>('');
  useEffect(() => { const timer = window.setTimeout(() => { const next = input.trim(); setQuery(next); setParams(current => { const copy = new URLSearchParams(current); next ? copy.set('q', next) : copy.delete('q'); activeTab === 'all' ? copy.delete('type') : copy.set('type', activeTab); return copy; }, { replace: true }); }, 250); return () => window.clearTimeout(timer); }, [input, activeTab, setParams]);
  const search = useQuery({ queryKey: ['global-search', query, location.trim(), profileType], queryFn: () => searchAll(query, location, profileType), enabled: query.length > 0, staleTime: 30_000, retry: 1 });
  const visible = useMemo(() => (search.data?.results ?? []).filter(result => {
    if (activeTab !== 'all' && result.kind !== activeTab) return false;
    const searchable = [result.title, result.subtitle, result.description, result.location, ...(result.skills ?? []), ...(result.technologies ?? []), result.experience].filter(Boolean).join(' ').toLowerCase();
    return (!skills.trim() || skills.toLowerCase().split(',').every(term => searchable.includes(term.trim()))) &&
      (!profileType || result.kind !== 'people' || result.profileType === profileType) &&
      (!location.trim() || result.kind === 'jobs' || searchable.includes(location.toLowerCase())) &&
      (!experience.trim() || searchable.includes(experience.toLowerCase())) &&
      (!technology.trim() || [...(result.technologies ?? []), ...(result.skills ?? []), result.description, result.title].filter(Boolean).join(' ').toLowerCase().includes(technology.toLowerCase()));
  }), [search.data, activeTab, skills, location, experience, technology]);
  const counts = useMemo(() => (search.data?.results ?? []).reduce<Record<string, number>>((out, result) => { out[result.kind] = (out[result.kind] ?? 0) + 1; return out; }, {}), [search.data]);
  const openResult = (result: Result) => result.kind === 'people' ? navigate(`/profile/${encodeURIComponent(String(result.id))}`) : result.kind === 'projects' ? navigate(`/projects/${result.id}`) : result.kind === 'jobs' ? navigate(`/jobs/${result.id}`) : navigate('/home');
  return <div className="workspace-page global-search-page">
    <div className="page-heading-row"><div><div className="page-kicker"><Search size={13}/> DISCOVER LINKHUB</div><h1>Search</h1><p>Find people, conversations, opportunities, and work.</p></div></div>
    <section className="global-search-shell">
      <label className="global-search-input"><Search size={20}/><input value={input} onChange={event => setInput(event.target.value)} placeholder="What are you looking for?" aria-label="Search LinkHub" autoFocus/><kbd>↵</kbd></label>
      <div className="global-search-tabs" role="tablist" aria-label="Search result type">{tabs.map(tab => <button key={tab.id} type="button" role="tab" aria-selected={activeTab === tab.id} className={activeTab === tab.id ? 'active' : ''} onClick={() => setActiveTab(tab.id)}>{tab.label}{tab.id !== 'all' && counts[tab.id] ? <span>{counts[tab.id]}</span> : null}</button>)}</div>
      <div className="search-layout">
        <aside className="global-search-filters"><button className="filter-heading" onClick={() => setFiltersOpen(value => !value)} aria-expanded={filtersOpen}><span><SlidersHorizontal size={15}/> Filters</span><span>{filtersOpen ? 'Hide' : 'Show'}</span></button>{filtersOpen && <div className="filter-fields"><label>Profile type<select value={profileType} onChange={event => setProfileType(event.target.value as ProfileType | '')}><option value="">Any profile type</option>{PROFILE_TYPES.map(type => <option key={type} value={type}>{type}</option>)}</select></label><label>Skills<input value={skills} onChange={event => setSkills(event.target.value)} placeholder="e.g. React, design"/></label><label>Location<input value={location} onChange={event => setLocation(event.target.value)} placeholder="City or remote"/></label><label>Experience<input value={experience} onChange={event => setExperience(event.target.value)} placeholder="e.g. senior, 5 years"/></label><label>Technology<input value={technology} onChange={event => setTechnology(event.target.value)} placeholder="e.g. TypeScript"/></label><button className="clear-filters" onClick={() => { setSkills(''); setLocation(''); setExperience(''); setTechnology(''); setProfileType(''); }}>Clear filters</button></div>}<p className="filter-note">Filters narrow the results returned by the LinkHub search APIs.</p></aside>
        <div className="global-search-results">
          {!query && <div className="search-prompt"><span><Search size={22}/></span><h2>Start with a search</h2><p>Search across member profiles, posts, jobs, and projects.</p></div>}
          {query && search.isFetching && <div className="search-loading" aria-live="polite">Searching LinkHub…</div>}
          {query && search.isError && <div className="search-state error-state"><h2>Search is unavailable</h2><p>{getApiErrorMessage(search.error, 'Could not reach the LinkHub search service.')}</p><Button size="sm" onClick={() => void search.refetch()}><RotateCw size={14}/> Try again</Button></div>}
          {query && search.isSuccess && <>
            {search.data.failedKinds.length > 0 && <div className="search-partial-error" role="status">Some result types could not be loaded: {search.data.failedKinds.join(', ')}.</div>}
            <div className="search-results-caption">{visible.length} {visible.length === 1 ? 'result' : 'results'} for <b>“{query}”</b></div>
            {visible.length === 0 ? <div className="search-state"><h2>No matches found</h2><p>Try a different phrase or clear one of the filters.</p></div> : <div className="global-result-list">{visible.map(result => { const Icon = kindIcon[result.kind]; return <article className="global-result-card" key={`${result.kind}-${result.id}`} onClick={() => openResult(result)} onKeyDown={event => { if (event.key === 'Enter') openResult(result); }} role="link" tabIndex={0}>
              <div className={`result-icon ${result.kind}`}>{result.image ? <img src={result.image} alt=""/> : <Icon size={18}/>}</div><div className="result-copy"><div className="result-overline">{result.kind}</div><h2>{result.title}</h2>{result.kind === 'people' && result.profileType && <span className="profile-role-badge">{result.profileType}</span>}{result.subtitle && <div className="result-subtitle">{result.subtitle}</div>}{result.description && <p>{result.description}</p>}
                {(result.location || result.skills?.length || result.technologies?.length) && <div className="result-tags">{result.location && <span><MapPin size={12}/>{result.location}</span>}{[...(result.skills ?? []), ...(result.technologies ?? [])].slice(0, 5).map(tag => <span key={tag}>{tag}</span>)}</div>}
              </div>{result.kind === 'people' ? <Link className="result-profile-link" to={`/profile/${result.id}`} onClick={event=>event.stopPropagation()}>View profile <ExternalLink size={13}/></Link> : <ExternalLink size={15} className="result-open" aria-hidden="true"/>}
            </article>; })}</div>}
          </>}
        </div>
      </div>
      <div className="search-api-note">Search uses the LinkHub people, posts, projects, and jobs endpoints.</div>
    </section>
  </div>;
}
