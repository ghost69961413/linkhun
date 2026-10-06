import { useEffect, useMemo, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, ArrowRight, ArrowUpRight, Check, Code2, ExternalLink, Eye, GitBranch, ImagePlus, LoaderCircle, Plus, Share2, Star, Trash2, Users, X } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { useAuthStore } from '../features/auth/authStore';
import { getApiErrorMessage } from '../utils/apiError';
import { projectService } from '../services/projectService';
import { repositoryService } from '../services/repositoryService';
import type { Project, ProjectPayload } from '../types/project';
import '../projects.css';

type Mode = 'list' | 'create' | 'edit' | 'detail';
const empty: ProjectPayload = { title: '', description: '', githubUrl: '', linkhubRepositoryId: '', liveDemoUrl: '', technologies: [], screenshots: [], features: [], teamMembers: [], visibility: 'PUBLIC' };
function splitList(text: string) { return [...new Set(text.split(/[\n,]/).map(item => item.trim()).filter(Boolean))]; }
function validUrl(text: string, required = false) { if (!text.trim()) return !required; try { const url = new URL(text); return ['http:', 'https:'].includes(url.protocol); } catch { return false; } }
function unownedName(project: Project, fallback?: string) { return project.owner?.fullName || project.owner?.name || project.ownerName || fallback || 'Owner unavailable'; }

export function ProjectsPage({ mode }: { mode: Mode }) {
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useAuthStore(s => s.user);
  const [form, setForm] = useState<ProjectPayload>(empty);
  const [techText, setTechText] = useState('');
  const [featureText, setFeatureText] = useState('');
  const [teamText, setTeamText] = useState('');
  const [screenshotPreviews, setScreenshotPreviews] = useState<string[]>([]);
  const [existingScreenshots, setExistingScreenshots] = useState<string[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [notice, setNotice] = useState('');
  const [deleteOpen, setDeleteOpen] = useState(false);
  const listQuery = useQuery({ queryKey: ['projects', 'mine', user?.id], queryFn: () => projectService.list(user?.id), enabled: mode === 'list' || mode === 'detail', retry: 1 });
  const projectQuery = useQuery({ queryKey: ['projects', id], queryFn: () => projectService.get(id), enabled: mode === 'edit' || mode === 'detail', retry: 1 });
  const repositoryOptions = useQuery({ queryKey: ['repositories', 'mine'], queryFn: repositoryService.listMine, enabled: mode === 'create' || mode === 'edit' });

  useEffect(() => {
    const urls = form.screenshots.map(file => URL.createObjectURL(file));
    setScreenshotPreviews(urls);
    return () => urls.forEach(URL.revokeObjectURL);
  }, [form.screenshots]);
  useEffect(() => {
    const project = projectQuery.data;
    if (!project || (mode !== 'edit' && mode !== 'detail')) return;
    setForm({ ...empty, title: project.title ?? '', description: project.description ?? '', githubUrl: project.githubUrl ?? '', linkhubRepositoryId: project.linkhubRepositoryId ?? '', liveDemoUrl: project.liveDemoUrl ?? '', technologies: project.technologies ?? [], features: project.features ?? [], teamMembers: project.teamMembers ?? [], visibility: project.visibility ?? 'PUBLIC' });
    setTechText((project.technologies ?? []).join(', ')); setFeatureText((project.features ?? []).join('\n')); setTeamText((project.teamMembers ?? []).join(', '));
    setExistingScreenshots([...(project.screenshots ?? []), ...(project.thumbnailUrl ? [project.thumbnailUrl] : [])]);
  }, [projectQuery.data, mode]);

  const createMutation = useMutation({ mutationFn: projectService.create, onSuccess: created => { queryClient.invalidateQueries({ queryKey: ['projects', 'mine'] }); queryClient.setQueryData(['projects', String(created.id)], created); navigate(`/projects/${created.id}`, { replace: true }); } });
  const updateMutation = useMutation({ mutationFn: (payload: Omit<ProjectPayload, 'screenshots'>) => projectService.update(id, payload), onSuccess: updated => { queryClient.invalidateQueries({ queryKey: ['projects', 'mine'] }); queryClient.setQueryData(['projects', id], updated); setNotice('Project updated.'); navigate(`/projects/${id}`); } });
  const deleteMutation = useMutation({ mutationFn: () => projectService.remove(id), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['projects', 'mine'] }); navigate('/projects', { replace: true }); } });

  function change<K extends keyof ProjectPayload>(key: K, value: ProjectPayload[K]) { setForm(current => ({ ...current, [key]: value })); setErrors(current => ({ ...current, [key]: '' })); setNotice(''); }
  function validate() {
    const next: Record<string, string> = {};
    if (!form.title.trim()) next.title = 'Enter a project name.';
    if (form.title.length > 120) next.title = 'Project names must be 120 characters or fewer.';
    if (!form.description.trim()) next.description = 'Add a project description.';
    if (form.description.length > 3000) next.description = 'Keep the description under 3,000 characters.';
    if (!validUrl(form.githubUrl)) next.githubUrl = 'Enter a valid GitHub URL.';
    if (!validUrl(form.liveDemoUrl)) next.liveDemoUrl = 'Enter a valid http or https URL.';
    setErrors(next); return Object.keys(next).length === 0;
  }
  function submit(event: React.FormEvent) {
    event.preventDefault(); if (!validate()) return;
    const payload = { ...form, title: form.title.trim(), description: form.description.trim(), githubUrl: form.githubUrl.trim(), liveDemoUrl: form.liveDemoUrl.trim(), technologies: splitList(techText), features: splitList(featureText), teamMembers: splitList(teamText) };
    if (mode === 'create') createMutation.mutate(payload);
    else {
      const { screenshots: _screenshots, ...editable } = payload;
      updateMutation.mutate(editable);
    }
  }
  async function share(project: Project) {
    const url = `${window.location.origin}/projects/${project.id}`;
    try { if (navigator.share) await navigator.share({ title: project.title, url }); else { await navigator.clipboard.writeText(url); setNotice('Project link copied.'); } }
    catch (error) { if ((error as Error).name !== 'AbortError') { try { await navigator.clipboard.writeText(url); setNotice('Project link copied.'); } catch { setNotice('Could not share this link from the current browser.'); } } }
  }

  if (mode === 'list') return <ProjectList projects={listQuery.data ?? []} loading={listQuery.isLoading} error={listQuery.isError ? getApiErrorMessage(listQuery.error, 'Could not load projects.') : ''} ownerName={user ? [user.firstName, user.lastName].filter(Boolean).join(' ') || user.username : undefined} onShare={share}/>;
  if (mode === 'create' || mode === 'edit') {
    const editing = mode === 'edit';
    if (editing && projectQuery.isLoading) return <ProjectLoading label="Loading project…"/>;
    if (editing && projectQuery.isError) return <ProjectError message={getApiErrorMessage(projectQuery.error, 'Could not load this project.')} onBack={() => navigate('/projects')}/>;
    const pending = createMutation.isPending || updateMutation.isPending;
    const mutationError = createMutation.error ?? updateMutation.error;
    return <div className="workspace-page project-editor-page"><div className="project-editor-head"><div><Link to={editing ? `/projects/${id}` : '/projects'} className="project-back"><ArrowLeft size={14}/>{editing ? 'Back to project' : 'All projects'}</Link><div className="page-kicker"><Code2 size={13}/>{editing ? 'PROJECT DETAILS' : 'MADE IN THE OPEN'}</div><h1>{editing ? 'Edit project' : 'Share a project'}</h1><p>Give people a clear picture of what you made and what went into it.</p></div></div>
      <form className="project-form surface-card" onSubmit={submit} noValidate>
        {mutationError && <div className="project-alert" role="alert">{getApiErrorMessage(mutationError, 'Could not save this project.')}</div>}{notice && <div className="project-alert success"><Check size={14}/>{notice}</div>}
        <div className="project-form-grid"><label className="project-field wide"><span>Project name <i>*</i></span><input maxLength={120} value={form.title} onChange={e => change('title', e.target.value)} placeholder="Name your project" aria-invalid={!!errors.title}/>{errors.title && <small>{errors.title}</small>}</label>
          <label className="project-field wide"><span>Description <i>*</i></span><textarea maxLength={3000} rows={6} value={form.description} onChange={e => change('description', e.target.value)} placeholder="What does it do? What problem does it solve?" aria-invalid={!!errors.description}/><div className="project-field-foot">{errors.description && <small>{errors.description}</small>}<small>{form.description.length}/3000</small></div></label>
          <label className="project-field wide"><span>Tech stack</span><input value={techText} onChange={e => { setTechText(e.target.value); change('technologies', splitList(e.target.value)); }} placeholder="React, TypeScript, PostgreSQL"/><small>Separate technologies with commas.</small>{splitList(techText).length > 0 && <div className="project-chips">{splitList(techText).map(tech => <span key={tech}>{tech}</span>)}</div>}</label>
          <label className="project-field"><span>GitHub URL <small>(optional)</small></span><div className="project-input-icon"><GitBranch size={14}/><input value={form.githubUrl} onChange={e => change('githubUrl', e.target.value)} placeholder="https://github.com/you/project" aria-invalid={!!errors.githubUrl}/></div>{errors.githubUrl && <small>{errors.githubUrl}</small>}</label>
          <label className="project-field"><span>LinkHub repository <small>(optional)</small></span><select value={form.linkhubRepositoryId ?? ''} onChange={e => change('linkhubRepositoryId', e.target.value ? Number(e.target.value) : '')}><option value="">No LinkHub repository</option>{(repositoryOptions.data ?? []).map(repo => <option key={repo.id} value={repo.id}>{repo.ownerUsername}/{repo.name} · {repo.visibility.toLowerCase()}</option>)}</select>{repositoryOptions.isError && <small>Could not load repositories. You can create the project and link a repository later.</small>}<small>Manage source files in your LinkHub repositories; GitHub is never required.</small></label>
          <label className="project-field"><span>Visibility</span><select value={form.visibility} onChange={e => change('visibility', e.target.value as ProjectPayload['visibility'])}><option value="PUBLIC">Public — visible on your profile</option><option value="PRIVATE">Private — only you can view it</option></select></label>
          <label className="project-field"><span>Live demo URL</span><div className="project-input-icon"><ExternalLink size={14}/><input value={form.liveDemoUrl} onChange={e => change('liveDemoUrl', e.target.value)} placeholder="https://your-project.com" aria-invalid={!!errors.liveDemoUrl}/></div>{errors.liveDemoUrl && <small>{errors.liveDemoUrl}</small>}</label>
          <label className="project-field wide"><span>Screenshots</span>{editing && existingScreenshots.length > 0 && <div className="project-existing-images">{existingScreenshots.map((src, i) => <img key={`${src}-${i}`} src={src} alt={`Project screenshot ${i + 1}`}/>)}</div>}{editing && <div className="project-field-note">The current project API accepts a single thumbnail on create. Screenshot gallery updates aren’t supported by the existing API.</div>}{!editing && <><div className="project-upload"><input type="file" accept="image/*" multiple onChange={e => { const files = [...(e.target.files ?? [])]; const invalid = files.find(file => !file.type.startsWith('image/') || file.size > 5 * 1024 * 1024); if (invalid) { setErrors(current => ({ ...current, screenshots: 'Select image files up to 5 MB each.' })); return; } change('screenshots', [...form.screenshots, ...files].slice(0, 6)); e.currentTarget.value = ''; }}/><ImagePlus size={17}/><b>Add screenshots</b><small>Image files, up to 5 MB each (maximum 6).</small></div>{errors.screenshots && <small className="project-error">{errors.screenshots}</small>}{screenshotPreviews.length > 0 && <div className="project-existing-images">{screenshotPreviews.map((src, i) => <div className="project-image-preview" key={`${src}-${i}`}><img src={src} alt={`Selected project screenshot ${i + 1}`}/><button type="button" onClick={() => change('screenshots', form.screenshots.filter((_, index) => index !== i))} aria-label={`Remove screenshot ${i + 1}`}><X size={13}/></button></div>)}</div>}</>}</label>
          <label className="project-field wide"><span>Features</span><textarea rows={3} value={featureText} onChange={e => { setFeatureText(e.target.value); change('features', splitList(e.target.value)); }} placeholder="Add one feature per line"/><small>Separate features with commas or new lines.</small></label>
          <label className="project-field wide"><span>Team members</span><input value={teamText} onChange={e => { setTeamText(e.target.value); change('teamMembers', splitList(e.target.value)); }} placeholder="Usernames or email addresses, separated by commas"/><small>Use the LinkHub usernames or contact addresses for collaborators.</small></label>
        </div><footer className="project-form-footer"><Button type="button" onClick={() => navigate(editing ? `/projects/${id}` : '/projects')}>Cancel</Button><Button type="submit" variant="primary" disabled={pending}>{pending ? <LoaderCircle className="project-spin" size={14}/> : <Check size={14}/>} {pending ? 'Saving…' : editing ? 'Save changes' : 'Create project'}</Button></footer>
      </form></div>;
  }
  if (projectQuery.isLoading) return <ProjectLoading label="Loading project…"/>;
  if (projectQuery.isError || !projectQuery.data) return <ProjectError message={getApiErrorMessage(projectQuery.error, 'This project could not be loaded.')} onBack={() => navigate('/projects')}/>;
  const project = projectQuery.data;
  const isOwner = (listQuery.data ?? []).some(item => String(item.id) === String(project.id)) || (!!user?.id && project.owner?.id === user.id) || (!!user?.id && project.owner?.userId === user.id);
  const detailOwner = isOwner ? ([user?.firstName, user?.lastName].filter(Boolean).join(' ') || user?.username) : undefined;
  return <div className="workspace-page project-detail-page"><div className="project-detail-toolbar"><Link to="/projects" className="project-back"><ArrowLeft size={14}/>All projects</Link><div className="project-toolbar-actions">{isOwner && <Button onClick={() => navigate(`/projects/edit/${project.id}`)}>Edit project</Button>}<Button onClick={() => share(project)}><Share2 size={14}/> Share</Button>{isOwner && <Button className="project-danger-button" onClick={() => setDeleteOpen(true)}><Trash2 size={14}/> Delete</Button>}</div></div>{notice && <div className="project-alert success">{notice}</div>}
    <article className="surface-card project-detail"><ProjectVisual project={project} large/><div className="project-detail-content"><div className="page-kicker"><Code2 size={13}/> LINKHUB PROJECT</div><h1>{project.title}</h1><div className="project-owner-line"><span className="project-owner-avatar">{project.owner?.profilePicture ? <img src={project.owner.profilePicture} alt=""/> : unownedName(project, detailOwner)[0]?.toUpperCase() ?? '?'}</span><span><small>Built by</small><b>{project.owner?.id ? <Link to={`/profile/${project.owner.id}`}>{unownedName(project, detailOwner)}</Link> : unownedName(project, detailOwner)}</b></span></div><p className="project-description">{project.description}</p><div className="project-detail-visibility">{project.visibility === 'PRIVATE' ? 'Private project · Only you can view this project' : 'Public project'}</div><div className="project-detail-metrics"><Metric Icon={Star} label="Stars" value={formatMetric(project.stars)}/><Metric Icon={Eye} label="Views" value={formatMetric(project.views)}/><Metric Icon={Code2} label="Technologies" value={String(project.technologies?.length ?? 0)}/><Metric Icon={Users} label="Team" value={String(project.teamMembers?.length ?? 0)}/></div>{project.technologies?.length > 0 && <section className="project-detail-section"><h2>Technologies</h2><div className="project-chips">{project.technologies.map(tech => <span key={tech}>{tech}</span>)}</div></section>}{project.features?.length ? <section className="project-detail-section"><h2>Features</h2><ul className="project-feature-list">{project.features.map(feature => <li key={feature}>{feature}</li>)}</ul></section> : null}{project.teamMembers?.length ? <section className="project-detail-section"><h2>Team members</h2><div className="project-chips">{project.teamMembers.map(member => <span key={member}>{member}</span>)}</div></section> : null}{(project.screenshots?.length || project.thumbnailUrl) ? <section className="project-detail-section"><h2>Screenshots</h2><div className="project-gallery">{[...(project.screenshots ?? []), ...(project.thumbnailUrl ? [project.thumbnailUrl] : [])].map((image, i) => <img key={`${image}-${i}`} src={image} alt={`${project.title} screenshot ${i + 1}`}/>)}</div></section> : null}<div className="project-links">{project.linkhubRepositoryPath && <Link to={project.linkhubRepositoryPath}><GitBranch size={15}/> Open LinkHub repository <ArrowUpRight size={13}/></Link>}{project.githubUrl && <a href={project.githubUrl} target="_blank" rel="noreferrer"><GitBranch size={15}/> View source <ArrowUpRight size={13}/></a>}{project.liveDemoUrl && <a href={project.liveDemoUrl} target="_blank" rel="noreferrer"><ExternalLink size={15}/> Live demo <ArrowUpRight size={13}/></a>}</div></div></article>{deleteOpen && <ConfirmDelete title={project.title} pending={deleteMutation.isPending} error={deleteMutation.error ? getApiErrorMessage(deleteMutation.error, 'Could not delete project.') : ''} onCancel={() => setDeleteOpen(false)} onConfirm={() => deleteMutation.mutate()}/>}</div>;
}

function ProjectList({ projects, loading, error, ownerName, onShare }: { projects: Project[]; loading: boolean; error: string; ownerName?: string; onShare: (project: Project) => void }) {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const filtered = useMemo(() => projects.filter(project => `${project.title} ${project.description} ${(project.technologies ?? []).join(' ')}`.toLowerCase().includes(query.toLowerCase())), [projects, query]);
  return <div className="workspace-page projects-list-page"><div className="page-heading-row"><div><div className="page-kicker"><Code2 size={13}/> MADE IN THE OPEN</div><h1>Your projects</h1><p>Show what you’ve been building and invite people into the work.</p></div><Button variant="primary" onClick={() => navigate('/projects/create')}><Plus size={14}/> Share a project</Button></div>{error && <div className="project-alert" role="alert">{error}</div>}<div className="projects-list-tools"><div className="projects-search"><Code2 size={15}/><input value={query} onChange={e => setQuery(e.target.value)} placeholder="Filter your projects" aria-label="Filter projects"/></div><span>{projects.length} {projects.length === 1 ? 'project' : 'projects'}</span></div>{loading ? <ProjectLoading label="Loading your projects…"/> : filtered.length === 0 ? <section className="surface-card projects-empty"><span><Code2 size={21}/></span><h2>{query ? 'No matching projects' : 'Your project space is ready.'}</h2><p>{query ? 'Try another name or technology.' : 'When you share a project, it will appear here. Add the work you want people to discover.'}</p>{!query && <Button variant="primary" onClick={() => navigate('/projects/create')}><Plus size={14}/> Create your first project</Button>}</section> : <div className="project-card-grid">{filtered.map(project => <ProjectCard key={project.id} project={project} ownerName={ownerName} onOpen={() => navigate(`/projects/${project.id}`)} onShare={() => onShare(project)}/>)}</div>}</div>;
}

function ProjectCard({ project, ownerName, onOpen, onShare }: { project: Project; ownerName?: string; onOpen: () => void; onShare: () => void }) {
  return <article className="surface-card project-card"><button type="button" className="project-card-open" onClick={onOpen} aria-label={`Open ${project.title}`}><ProjectVisual project={project}/></button><div className="project-card-body"><div className="project-card-heading"><button onClick={onOpen}><h2>{project.title}</h2></button><button className="project-share" onClick={onShare} aria-label={`Share ${project.title}`}><Share2 size={14}/></button></div><p>{project.description}</p><div className="project-chips">{(project.technologies ?? []).slice(0, 4).map(tech => <span key={tech}>{tech}</span>)}{(project.technologies ?? []).length > 4 && <span>+{project.technologies.length - 4}</span>}</div><div className="project-card-owner"><span className="project-owner-avatar">{unownedName(project, ownerName)[0]?.toUpperCase() ?? '?'}</span><div><small>Owner</small><b>{unownedName(project, ownerName)}</b></div></div><footer className="project-card-footer"><span><Star size={13}/>{formatMetric(project.stars)}</span><span><Eye size={13}/>{formatMetric(project.views)}</span><span><Code2 size={13}/>{project.technologies?.length ?? 0} tech</span><button onClick={onOpen} aria-label="View project"><ArrowRight size={14}/></button></footer></div></article>;
}

function ProjectVisual({ project, large = false }: { project: Project; large?: boolean }) { return <div className={`project-visual ${large ? 'large' : ''}`} style={project.thumbnailUrl ? { backgroundImage: `linear-gradient(0deg,#080a11a6,transparent),url("${project.thumbnailUrl}")` } : undefined}>{!project.thumbnailUrl && <span className="project-visual-mark"><Code2 size={large ? 32 : 25}/></span>}{large && project.screenshots?.length ? <span className="project-image-count">{project.screenshots.length} screenshots</span> : null}</div>; }
function formatMetric(value?: number | null) { return value == null ? '—' : new Intl.NumberFormat(undefined, { notation: value > 999 ? 'compact' : 'standard', maximumFractionDigits: 1 }).format(value); }
function Metric({ Icon, label, value }: { Icon: typeof Star; label: string; value: string }) { return <div className="project-detail-metric"><span><Icon size={14}/>{label}</span><b>{value}</b></div>; }
function ProjectLoading({ label }: { label: string }) { return <div className="workspace-page project-loading"><LoaderCircle className="project-spin" size={17}/>{label}</div>; }
function ProjectError({ message, onBack }: { message: string; onBack: () => void }) { return <div className="workspace-page"><section className="surface-card projects-empty"><h2>Project unavailable</h2><p>{message}</p><Button onClick={onBack}>Back to projects</Button></section></div>; }
function ConfirmDelete({ title, pending, error, onCancel, onConfirm }: { title: string; pending: boolean; error: string; onCancel: () => void; onConfirm: () => void }) { return <div className="project-modal-scrim" role="presentation"><section className="project-confirm surface-card" role="alertdialog" aria-modal="true" aria-labelledby="delete-title"><button className="project-modal-close" onClick={onCancel} aria-label="Close"><X size={15}/></button><span className="project-danger-icon"><Trash2 size={18}/></span><h2 id="delete-title">Delete this project?</h2><p><b>{title}</b> will be removed from LinkHub. This can’t be undone.</p>{error && <div className="project-alert">{error}</div>}<div className="project-confirm-actions"><Button onClick={onCancel}>Keep project</Button><Button className="project-danger-button" disabled={pending} onClick={onConfirm}>{pending ? 'Deleting…' : 'Delete project'}</Button></div></section></div>; }
