import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { AlertCircle, ArrowLeft, BookOpen, ChevronDown, Code2, Download, ExternalLink, File, FilePlus2, Folder, FolderPlus, GitBranch, GitCommitHorizontal, LockKeyhole, LoaderCircle, MoreHorizontal, Plus, Search, Shield, Trash2, Upload, Users, Globe2 } from 'lucide-react';
import { Button } from '../components/ui/Button';
import { repositoryService } from '../services/repositoryService';
import type { LinkHubRepository, RepositoryCreateInput, RepositoryFile } from '../types/repository';
import { getApiErrorMessage } from '../utils/apiError';
import '../repositories.css';

const empty: RepositoryCreateInput = { name: '', description: '', visibility: 'PUBLIC', initializeReadme: true, license: 'None', gitignoreTemplate: 'None' };
type RepoTab = 'Code' | 'Issues' | 'Pull requests' | 'Commits';
const tabs: RepoTab[] = ['Code', 'Issues', 'Pull requests', 'Commits'];

export function RepositoriesPage({ mode }: { mode: 'list' | 'create' | 'detail' }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const params = useParams();
  const owner = params.owner ?? '';
  const name = params.repo ?? '';
  const mine = useQuery({ queryKey: ['repositories', 'mine'], queryFn: repositoryService.listMine });
  const publicRepos = useQuery({ queryKey: ['repositories', 'public'], queryFn: repositoryService.listPublic, enabled: mode === 'list' });
  const detail = useQuery({ queryKey: ['repository', owner, name], queryFn: () => repositoryService.get(owner, name), enabled: mode === 'detail' && !!owner && !!name, retry: false });
  const [form, setForm] = useState<RepositoryCreateInput>(empty);
  const [filter, setFilter] = useState('');
  const [error, setError] = useState('');
  const create = useMutation({ mutationFn: repositoryService.create, onSuccess: repo => { queryClient.invalidateQueries({ queryKey: ['repositories'] }); navigate(`/repositories/${repo.ownerUsername}/${repo.name}`, { replace: true }); } });

  if (mode === 'create') {
    const pending = create.isPending;
    function submit(event: React.FormEvent) {
      event.preventDefault(); setError('');
      if (!/^[A-Za-z0-9._-]{1,100}$/.test(form.name.trim())) { setError('Use 1–100 letters, numbers, dots, underscores, or hyphens.'); return; }
      create.mutate({ ...form, name: form.name.trim(), description: form.description.trim() });
    }
    return <div className="workspace-page repo-create-page"><div className="repo-heading"><Link to="/repositories" className="repo-back"><ArrowLeft size={15}/>All repositories</Link><div className="repo-eyebrow"><GitBranch size={14}/> LINKHUB SOURCE CONTROL</div><h1>Create a repository</h1><p>A private or public code repository hosted on LinkHub.</p></div><form className="repo-create-card surface-card" onSubmit={submit}>
      {(error || create.isError) && <div className="repo-alert"><AlertCircle size={16}/>{error || getApiErrorMessage(create.error, 'Could not create repository.')}</div>}
      <label className="repo-field"><span>Repository name <i>*</i></span><input autoFocus maxLength={100} value={form.name} onChange={e => setForm(v => ({ ...v, name: e.target.value }))} placeholder="my-awesome-project"/><small>Letters, numbers, dots, underscores, and hyphens.</small></label>
      <label className="repo-field"><span>Description</span><textarea maxLength={1000} rows={3} value={form.description} onChange={e => setForm(v => ({ ...v, description: e.target.value }))} placeholder="What are you building?"/></label>
      <div className="repo-form-row"><label className="repo-field"><span>Visibility</span><select value={form.visibility} onChange={e => setForm(v => ({ ...v, visibility: e.target.value as RepositoryCreateInput['visibility'] }))}><option value="PUBLIC">Public</option><option value="PRIVATE">Private</option></select><small>{form.visibility === 'PUBLIC' ? 'Visible to LinkHub members.' : 'Only you can view this repository.'}</small></label><label className="repo-field"><span>License</span><select value={form.license} onChange={e => setForm(v => ({ ...v, license: e.target.value }))}><option>None</option><option>MIT</option></select></label></div>
      <label className="repo-field"><span>.gitignore template</span><select value={form.gitignoreTemplate} onChange={e => setForm(v => ({ ...v, gitignoreTemplate: e.target.value }))}><option>None</option><option>Node</option><option>React</option><option>Java</option><option>Python</option></select></label>
      <label className="repo-check"><input type="checkbox" checked={form.initializeReadme} onChange={e => setForm(v => ({ ...v, initializeReadme: e.target.checked }))}/><span><b>Initialize this repository with a README</b><small>Creates README.md on the main branch.</small></span></label>
      <footer><Link to="/repositories">Cancel</Link><Button variant="primary" type="submit" disabled={pending}>{pending ? <LoaderCircle size={15} className="repo-spin"/> : <Plus size={15}/>} Create repository</Button></footer>
    </form></div>;
  }

  if (mode === 'list') {
    const repos = new Map<number, LinkHubRepository>();
    [...(mine.data ?? []), ...(publicRepos.data ?? [])].forEach(repo => repos.set(repo.id, repo));
    const visible = [...repos.values()].filter(repo => `${repo.name} ${repo.description ?? ''} ${repo.ownerUsername}`.toLowerCase().includes(filter.toLowerCase()));
    const loadError = mine.isError ? mine.error : publicRepos.isError ? publicRepos.error : null;
    return <div className="workspace-page repo-list-page"><div className="repo-list-heading"><div><div className="repo-eyebrow"><GitBranch size={14}/> YOUR CODE, ON LINKHUB</div><h1>Repositories</h1><p>Create and manage source code repositories without leaving LinkHub.</p></div><Button variant="primary" onClick={() => navigate('/repositories/create')}><Plus size={15}/> New repository</Button></div>
      {loadError && <div className="repo-alert"><AlertCircle size={16}/>{getApiErrorMessage(loadError, 'Could not load repositories.')}</div>}
      <div className="repo-list-tools"><label><Search size={15}/><input value={filter} onChange={e => setFilter(e.target.value)} placeholder="Find a repository"/></label><span>{visible.length} {visible.length === 1 ? 'repository' : 'repositories'}</span></div>
      {mine.isLoading || publicRepos.isLoading ? <div className="repo-empty surface-card"><LoaderCircle className="repo-spin"/> Loading repositories…</div> : visible.length ? <div className="repo-cards">{visible.map(repo => <RepositoryCard key={repo.id} repo={repo} onOpen={() => navigate(`/repositories/${repo.ownerUsername}/${repo.name}`)}/>)}</div> : <section className="repo-empty surface-card"><div className="repo-empty-icon"><GitBranch size={22}/></div><h2>No repositories yet</h2><p>Create a repository to store source code, documentation, and project files on LinkHub.</p><Button variant="primary" onClick={() => navigate('/repositories/create')}><Plus size={15}/> Create a repository</Button></section>}
    </div>;
  }

  if (detail.isLoading) return <div className="workspace-page repo-loading"><LoaderCircle size={17} className="repo-spin"/> Loading repository…</div>;
  if (detail.isError || !detail.data) return <div className="workspace-page"><section className="repo-empty surface-card"><h2>Repository unavailable</h2><p>{detail.error ? getApiErrorMessage(detail.error, 'This repository could not be loaded.') : 'This repository could not be found.'}</p><Button onClick={() => navigate('/repositories')}>Browse repositories</Button></section></div>;
  return <RepositoryDetail repo={detail.data} mine={mine.data ?? []}/>;
}

function RepositoryCard({ repo, onOpen }: { repo: LinkHubRepository; onOpen: () => void }) {
  return <button className="repo-card surface-card" onClick={onOpen}><div className="repo-card-top"><span className="repo-card-glyph"><GitBranch size={18}/></span><span className={`repo-visibility ${repo.visibility.toLowerCase()}`}>{repo.visibility === 'PRIVATE' ? <LockKeyhole size={12}/> : <Globe2 size={12}/>} {repo.visibility}</span></div><b>{repo.ownerUsername} / <strong>{repo.name}</strong></b><p>{repo.description || 'No description provided.'}</p><footer><span><File size={13}/>{repo.filesCount} files</span><span><GitBranch size={13}/>{repo.defaultBranch}</span><span>{repo.license || 'No license'}</span></footer></button>;
}

function RepositoryDetail({ repo, mine }: { repo: LinkHubRepository; mine: LinkHubRepository[] }) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const owner = mine.some(item => item.id === repo.id);
  const [branch, setBranch] = useState(repo.defaultBranch || 'main');
  const [tab, setTab] = useState<RepoTab>('Code');
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [editing, setEditing] = useState(false);
  const [draft, setDraft] = useState('');
  const [createMode, setCreateMode] = useState<'file' | 'folder' | null>(null);
  const [newPath, setNewPath] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newBranchName, setNewBranchName] = useState('');
  const [showBranchForm, setShowBranchForm] = useState(false);
  const [error, setError] = useState('');
  const files = useQuery({ queryKey: ['repository', repo.id, 'files', branch], queryFn: () => repositoryService.files(repo.id, branch) });
  const branches = useQuery({ queryKey: ['repository', repo.id, 'branches'], queryFn: () => repositoryService.branches(repo.id) });
  const commits = useQuery({ queryKey: ['repository', repo.id, 'commits', branch], queryFn: () => repositoryService.commits(repo.id, branch), enabled: tab === 'Commits' });
  const selected = useQuery({ queryKey: ['repository', repo.id, 'file', selectedId], queryFn: () => repositoryService.file(repo.id, selectedId!), enabled: selectedId != null });
  const refresh = () => { queryClient.invalidateQueries({ queryKey: ['repository', repo.ownerUsername, repo.name] }); queryClient.invalidateQueries({ queryKey: ['repository', repo.id] }); };
  const mutation = useMutation({ mutationFn: async () => {
    if (!newPath.trim()) throw new Error('Enter a file or folder path.');
    if (createMode === 'folder') return repositoryService.createFile(repo.id, branch, newPath, '', true);
    return repositoryService.createFile(repo.id, branch, newPath, newContent, false);
  }, onSuccess: item => { setCreateMode(null); setNewPath(''); setNewContent(''); setSelectedId(item.directory ? null : item.id); setError(''); refresh(); } });
  const saveMutation = useMutation({ mutationFn: () => repositoryService.updateFile(repo.id, selectedId!, draft), onSuccess: saved => { setDraft(saved.content ?? ''); setEditing(false); setError(''); refresh(); queryClient.setQueryData(['repository', repo.id, 'file', saved.id], saved); } });
  const deleteRepo = useMutation({ mutationFn: () => repositoryService.remove(repo.id), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['repositories'] }); navigate('/repositories'); } });
  const branchMutation = useMutation({ mutationFn: () => repositoryService.createBranch(repo.id, newBranchName, branch), onSuccess: name => { setBranch(name); setNewBranchName(''); setShowBranchForm(false); queryClient.invalidateQueries({ queryKey: ['repository', repo.id, 'branches'] }); refresh(); } });
  useEffect(() => { if (selected.data) setDraft(selected.data.content ?? ''); }, [selected.data]);
  const languageCounts = useMemo(() => { const counts = new Map<string, number>(); (files.data ?? []).filter(f => !f.directory).forEach(f => { const ext = f.path.split('.').pop()?.toLowerCase(); const name = ({ ts: 'TypeScript', tsx: 'TypeScript', js: 'JavaScript', jsx: 'JavaScript', java: 'Java', py: 'Python', css: 'CSS', html: 'HTML', md: 'Markdown', json: 'JSON', sql: 'SQL' } as Record<string,string>)[ext ?? '']; if (name) counts.set(name, (counts.get(name) ?? 0) + 1); }); return [...counts.entries()].sort((a,b) => b[1]-a[1]); }, [files.data]);
  const readme = (files.data ?? []).find(file => file.path.toLowerCase() === 'readme.md');

  async function uploadFiles(input: FileList | null) {
    if (!input?.length) return; setError('');
    try {
      for (const file of Array.from(input)) {
        const relative = (file as File & { webkitRelativePath?: string }).webkitRelativePath || file.name;
        const filePath = relative.split('/').slice(1).join('/') || relative;
        await repositoryService.upload(repo.id, branch, file, filePath);
      }
      refresh();
    } catch (e) { setError(getApiErrorMessage(e, 'Could not upload source files.')); }
    finally { const inputEl = document.getElementById('repo-file-upload') as HTMLInputElement | null; if (inputEl) inputEl.value = ''; }
  }
  async function removeFile(file: RepositoryFile) {
    if (!window.confirm(`Delete ${file.path}${file.directory ? ' and everything inside it' : ''}?`)) return;
    try { await repositoryService.removeFile(repo.id, file.id); setSelectedId(null); refresh(); } catch (e) { setError(getApiErrorMessage(e, 'Could not delete this file.')); }
  }
  async function downloadFile(file: RepositoryFile) {
    try { const blob=await repositoryService.download(repo.id,file.id); const url=URL.createObjectURL(blob); const anchor=document.createElement('a'); anchor.href=url; anchor.download=file.path.split('/').pop() || file.path; anchor.click(); URL.revokeObjectURL(url); }
    catch (e) { setError(getApiErrorMessage(e, 'Could not download this file.')); }
  }
  async function removeRepository() {
    if (!window.confirm(`Permanently delete ${repo.name} and all of its files?`)) return;
    deleteRepo.mutate();
  }

  return <div className="workspace-page repo-detail-page">
    <div className="repo-detail-top"><Link className="repo-back" to="/repositories"><ArrowLeft size={15}/>Repositories</Link><div className="repo-detail-actions">{repo.repositoryUrl && <a className="repo-github-link" href={repo.repositoryUrl} target="_blank" rel="noreferrer"><ExternalLink size={14}/> View on GitHub</a>}{owner && <button className="repo-delete-button" onClick={removeRepository}><Trash2 size={14}/> Delete repository</button>}</div></div>
    <header className="repo-detail-heading"><div className="repo-detail-title"><span className="repo-card-glyph"><GitBranch size={18}/></span><h1><Link to={`/profile/${repo.ownerId}`}>{repo.ownerUsername}</Link><span>/</span>{repo.name}</h1><span className={`repo-visibility ${repo.visibility.toLowerCase()}`}>{repo.visibility === 'PRIVATE' ? <LockKeyhole size={12}/> : <Globe2 size={12}/>} {repo.visibility}</span></div><p>{repo.description || 'No description provided.'}</p><div className="repo-detail-meta"><span><StarIcon/>{repo.stars ?? 0} stars</span><span><Users size={14}/>{repo.forks ?? 0} forks</span><span><GitBranch size={14}/>{branches.data?.length ?? 1} branches</span><span><File size={14}/>{files.data?.filter(f => !f.directory).length ?? repo.filesCount} files</span></div></header>
    <nav className="repo-tabs">{tabs.map(label => <button key={label} className={tab === label ? 'active' : ''} onClick={() => setTab(label)}>{label === 'Code' ? <Code2 size={15}/> : label === 'Commits' ? <GitCommitHorizontal size={15}/> : <MoreHorizontal size={15}/>} {label}{label === 'Code' && <b>{files.data?.length ?? repo.filesCount}</b>}</button>)}</nav>
    {error && <div className="repo-alert"><AlertCircle size={15}/>{error}<button onClick={() => setError('')}>Dismiss</button></div>}
    {tab === 'Code' && <div className="repo-layout"><main className="repo-code-column"><div className="repo-code-toolbar"><label className="repo-branch-select"><GitBranch size={15}/><select aria-label="Select branch" value={branch} onChange={e => { setBranch(e.target.value); setSelectedId(null); }}><option value="" disabled>Branch</option>{(branches.data ?? [repo.defaultBranch]).map(value => <option key={value}>{value}</option>)}</select><ChevronDown size={13}/></label>{owner && <div className="repo-code-actions"><button onClick={() => setShowBranchForm(v => !v)}><GitBranch size={14}/> New branch</button><button onClick={() => { setCreateMode('file'); setNewPath(''); setNewContent(''); }}><FilePlus2 size={14}/> Add file</button><button onClick={() => { setCreateMode('folder'); setNewPath(''); }}><FolderPlus size={14}/> Add folder</button><label><Upload size={14}/> Upload files<input id="repo-file-upload" type="file" multiple onChange={e => void uploadFiles(e.target.files)}/></label></div>}</div>
        {showBranchForm && owner && <div className="repo-branch-form"><input value={newBranchName} onChange={e => setNewBranchName(e.target.value)} placeholder="feature/my-change"/><Button size="sm" variant="primary" disabled={branchMutation.isPending} onClick={() => branchMutation.mutate()}>{branchMutation.isPending ? 'Creating…' : 'Create branch'}</Button><button onClick={() => setShowBranchForm(false)}>Cancel</button>{branchMutation.isError && <small>{getApiErrorMessage(branchMutation.error,'Could not create branch.')}</small>}</div>}
        {mutation.isError && <div className="repo-alert"><AlertCircle size={15}/>{getApiErrorMessage(mutation.error,'Could not save file.')}</div>}
        {createMode && <div className="repo-editor-card"><div className="repo-editor-head"><b>{createMode === 'folder' ? 'Create folder' : 'Create file'}</b><button onClick={() => setCreateMode(null)}>Cancel</button></div><input value={newPath} onChange={e => setNewPath(e.target.value)} placeholder={createMode === 'folder' ? 'src/components' : 'src/index.ts'}/>{createMode === 'file' && <textarea spellCheck={false} value={newContent} onChange={e => setNewContent(e.target.value)} placeholder="Write file contents…"/>}<Button variant="primary" disabled={mutation.isPending} onClick={() => mutation.mutate()}>{mutation.isPending ? 'Saving…' : createMode === 'folder' ? 'Create folder' : 'Commit new file'}</Button></div>}
        <div className="repo-file-list"><div className="repo-file-list-head"><span><GitCommitHorizontal size={14}/> {owner ? 'You' : repo.ownerUsername} <small>{files.data?.length ? 'updated the repository' : 'created this repository'}</small></span><span>{branch}</span></div>{files.isLoading ? <div className="repo-file-empty"><LoaderCircle className="repo-spin"/> Loading files…</div> : !files.data?.length ? <div className="repo-file-empty"><Folder size={22}/><b>This repository is empty</b><span>Create a file or upload source code to get started.</span></div> : files.data.map(file => <div className={`repo-file-row ${selectedId === file.id ? 'selected' : ''}`} key={file.id}><button className="repo-file-open" onClick={() => { if (!file.directory) { setSelectedId(file.id); setEditing(false); } }} style={{ paddingLeft: `${14 + Math.max(0,file.path.split('/').length-1)*17}px` }}>{file.directory ? <Folder size={15}/> : <File size={15}/>}<span>{file.path}</span></button><small>{file.directory ? 'folder' : `${formatBytes(file.size)}`}</small>{owner && <button className="repo-file-remove" aria-label={`Delete ${file.path}`} onClick={() => void removeFile(file)}><Trash2 size={13}/></button>}</div>)}</div>
        {selectedId != null && <section className="repo-source-view"><header><div><File size={14}/><b>{selected.data?.path ?? 'Loading file…'}</b><small>{selected.data?.size != null ? formatBytes(selected.data.size) : ''}</small></div>{selected.data?.binary ? <button onClick={() => void downloadFile(selected.data!)}><Download size={14}/> Download file</button> : owner && selected.data && <div>{editing ? <><button onClick={() => { setEditing(false); setDraft(selected.data?.content ?? ''); }}>Cancel</button><Button variant="primary" size="sm" disabled={saveMutation.isPending} onClick={() => saveMutation.mutate()}>{saveMutation.isPending ? 'Saving…' : 'Commit changes'}</Button></> : <button onClick={() => { setDraft(selected.data?.content ?? ''); setEditing(true); }}>Edit file</button>}</div>}</header>{selected.isLoading ? <div className="repo-file-empty"><LoaderCircle className="repo-spin"/> Loading file…</div> : selected.isError ? <div className="repo-alert">{getApiErrorMessage(selected.error,'Could not load file.')}</div> : selected.data?.binary ? <div className="repo-file-empty"><File size={21}/><b>Binary file</b><span>{selected.data.mimeType || 'Download this file to open it.'}</span></div> : editing ? <textarea className="repo-code-editor" spellCheck={false} value={draft} onChange={e => setDraft(e.target.value)}/> : <pre className="repo-code-content"><code>{selected.data?.content ?? ''}</code></pre>}{saveMutation.isError && <div className="repo-alert">{getApiErrorMessage(saveMutation.error,'Could not save changes.')}</div>}</section>}
        {readme && selectedId == null && <section className="repo-readme"><header><BookOpen size={15}/><b>README.md</b><span>Preview</span></header><ReadmePreview repoId={repo.id} fileId={readme.id}/></section>}
      </main><aside className="repo-sidebar"><section><h3>About</h3><p>{repo.description || 'No description provided.'}</p><div className="repo-sidebar-tags"><span>{repo.visibility === 'PRIVATE' ? <Shield size={13}/> : <Globe2 size={13}/>} {repo.visibility === 'PRIVATE' ? 'Private' : 'Public'}</span><span><GitBranch size={13}/> {repo.defaultBranch}</span>{repo.license && repo.license !== 'None' && <span>{repo.license} license</span>}</div></section><section><h3>Languages</h3>{languageCounts.length ? languageCounts.map(([language,count])=><div className="repo-language" key={language}><span>{language}</span><small>{count} {count===1?'file':'files'}</small></div>) : <p className="repo-muted">No language data yet. Add source files to see languages here.</p>}</section><section><h3>LinkHub repository</h3><p>Files and history are stored by LinkHub. GitHub is optional.</p>{repo.repositoryUrl && <a href={repo.repositoryUrl} target="_blank" rel="noreferrer"><ExternalLink size={14}/> View on GitHub</a>}</section><section className="repo-sidebar-stat"><span><StarIcon/> Stars</span><b>{repo.stars ?? 0}</b><span><Users size={14}/> Forks</span><b>{repo.forks ?? 0}</b></section></aside></div>}
    {tab === 'Commits' && <section className="repo-commits surface-card">{commits.isLoading ? <div className="repo-file-empty"><LoaderCircle className="repo-spin"/> Loading history…</div> : commits.data?.length ? commits.data.map(commit=><article key={commit.id}><span className="repo-commit-icon"><GitCommitHorizontal size={16}/></span><div><b>{commit.message}</b><p>{commit.author} committed to <code>{commit.branch}</code>{commit.changeSummary ? ` · ${commit.changeSummary}` : ''}</p></div><time>{new Date(commit.createdAt).toLocaleString()}</time></article>) : <div className="repo-file-empty">No commits on this branch yet.</div>}</section>}
    {(tab === 'Issues' || tab === 'Pull requests') && <section className="repo-coming surface-card"><div><MoreHorizontal size={19}/></div><h2>{tab} are not available yet</h2><p>Repository files, branches, and commit history are live. {tab} tracking is not implemented, so there is no placeholder data here.</p></section>}
  </div>;
}

function ReadmePreview({ repoId, fileId }: { repoId: number; fileId: number }) {
  const file = useQuery({ queryKey: ['repository', repoId, 'file', fileId], queryFn: () => repositoryService.file(repoId, fileId) });
  if (file.isLoading) return <div className="repo-markdown">Loading README…</div>;
  const lines = (file.data?.content ?? '').split('\n');
  const blocks: React.ReactNode[] = [];
  let code: string[] = [];
  let codeLanguage = '';
  let inCode = false;
  lines.forEach((line, index) => {
    if (line.startsWith('```')) {
      if (!inCode) { inCode = true; codeLanguage = line.slice(3).trim(); code = []; }
      else { blocks.push(<pre key={`code-${index}`}><code className={codeLanguage ? `language-${codeLanguage}` : undefined}>{code.join('\n')}</code></pre>); inCode = false; }
      return;
    }
    if (inCode) { code.push(line); return; }
    const heading = /^(#{1,6})\s+(.+)$/.exec(line);
    const bullet = /^\s*[-*+]\s+(.+)$/.exec(line);
    const ordered = /^\s*\d+\.\s+(.+)$/.exec(line);
    if (heading) blocks.push(<MarkdownHeading key={index} level={heading[1].length}>{inlineMarkdown(heading[2])}</MarkdownHeading>);
    else if (bullet) blocks.push(<div className="repo-markdown-list" key={index}><span>•</span>{inlineMarkdown(bullet[1])}</div>);
    else if (ordered) blocks.push(<div className="repo-markdown-list" key={index}><span>{ordered[0].trim().match(/^\d+/)?.[0]}.</span>{inlineMarkdown(ordered[1])}</div>);
    else if (line.trim()) blocks.push(<p key={index}>{inlineMarkdown(line)}</p>);
  });
  if (inCode) blocks.push(<pre key="code-unclosed"><code>{code.join('\n')}</code></pre>);
  return <div className="repo-markdown">{blocks}</div>;
}
function MarkdownHeading({ level, children }: { level: number; children: React.ReactNode }) {
  if (level === 1) return <h1>{children}</h1>;
  if (level === 2) return <h2>{children}</h2>;
  if (level === 3) return <h3>{children}</h3>;
  if (level === 4) return <h4>{children}</h4>;
  if (level === 5) return <h5>{children}</h5>;
  return <h6>{children}</h6>;
}
function inlineMarkdown(text: string) {
  return text.split(/(`[^`]+`|\*\*[^*]+\*\*|\*[^*]+\*)/g).map((part, index) => {
    if (part.startsWith('`') && part.endsWith('`')) return <code key={index}>{part.slice(1,-1)}</code>;
    if (part.startsWith('**') && part.endsWith('**')) return <strong key={index}>{part.slice(2,-2)}</strong>;
    if (part.startsWith('*') && part.endsWith('*')) return <em key={index}>{part.slice(1,-1)}</em>;
    return part;
  });
}
function StarIcon() { return <span className="repo-star-icon">★</span>; }
function formatBytes(value: number) { if (!value) return '0 B'; if (value < 1024) return `${value} B`; return `${(value / 1024).toFixed(1)} KB`; }
