import { useEffect, useMemo, useRef, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { ArrowDown, ArrowUpRight, Check, ChevronDown, Code2, ExternalLink, Eye, Globe2, ImagePlus, LoaderCircle, MessageCircle, MoreHorizontal, Send, Share2, Sparkles, ThumbsUp, Trash2, Users, X } from 'lucide-react';
import { useAuthStore } from '../../features/auth/authStore';
import { Button } from '../../components/ui/Button';
import { getApiErrorMessage } from '../../utils/apiError';
import { useCreateComment, useCreatePost, useDeletePost, usePostFeed, usePostLike } from '../../hooks/usePostFeed';
import { postService } from '../../services/postService';
import type { Comment, Post } from '../../types/post';
import '../../feed.css';

function authorOf(post: Post) {
  const name = post.fullName || post.author?.fullName || [post.author?.firstName].filter(Boolean).join(' ') || post.username || post.author?.username || 'LinkHub member';
  const username = post.username || post.author?.username;
  const image = post.author?.profilePicture;
  return { name, username, image, userId: post.author?.userId ?? post.author?.id ?? post.userId, initials: name.split(/\s+/).slice(0, 2).map(part => part[0]).join('').toUpperCase() };
}
function dateLabel(value?: string) {
  if (!value) return '';
  const date = new Date(value); if (Number.isNaN(date.getTime())) return '';
  const seconds = Math.max(0, Math.floor((Date.now() - date.getTime()) / 1000));
  if (seconds < 60) return 'just now'; if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`; if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
  return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: date.getFullYear() === new Date().getFullYear() ? undefined : 'numeric' });
}

export function HomeFeed() {
  const user = useAuthStore(s => s.user);
  const [content, setContent] = useState('');
  const [visibility, setVisibility] = useState('PUBLIC');
  const [media, setMedia] = useState<File | null>(null);
  const [mediaPreview, setMediaPreview] = useState('');
  const [formError, setFormError] = useState('');
  const [mediaError, setMediaError] = useState('');
  const sentinel = useRef<HTMLDivElement>(null);
  const feed = usePostFeed();
  const create = useCreatePost();

  useEffect(() => { if (!media) { setMediaPreview(''); return; } const url = URL.createObjectURL(media); setMediaPreview(url); return () => URL.revokeObjectURL(url); }, [media]);
  useEffect(() => {
    const node = sentinel.current; if (!node || !feed.hasNextPage || feed.isFetchingNextPage) return;
    const observer = new IntersectionObserver(entries => { if (entries[0]?.isIntersecting) void feed.fetchNextPage(); }, { rootMargin: '500px 0px' });
    observer.observe(node); return () => observer.disconnect();
  }, [feed.fetchNextPage, feed.hasNextPage, feed.isFetchingNextPage]);

  const posts = useMemo(() => feed.data?.pages.flatMap(page => page.content) ?? [], [feed.data]);
  function chooseMedia(file?: File) {
    if (!file) return;
    const allowed = file.type.startsWith('image/') || file.type.startsWith('video/');
    if (!allowed) { setMediaError('Choose an image or video file.'); return; }
    const maxSize = file.type.startsWith('image/') ? 5 * 1024 * 1024 : 25 * 1024 * 1024;
    if (file.size > maxSize) { setMediaError(file.type.startsWith('image/') ? 'Images must be 5 MB or smaller.' : 'Videos must be 25 MB or smaller.'); return; }
    setMedia(file); setMediaError('');
  }
  function submitPost() {
    if (!content.trim()) { setFormError('Write something before sharing your post.'); return; }
    if (content.trim().length > 5000) { setFormError('Posts can be up to 5,000 characters.'); return; }
    setFormError('');
    create.mutate({ content: content.trim(), visibility, media }, { onSuccess: () => { setContent(''); setMedia(null); setVisibility('PUBLIC'); } });
  }
  const postError = feed.isError ? getApiErrorMessage(feed.error, 'Could not load the feed.') : '';
  const authorName = user ? [user.firstName, user.lastName].filter(Boolean).join(' ') || user.username : 'Your post';

  return <div className="workspace-page home-feed-page"><div className="feed-heading"><div><div className="page-kicker"><Sparkles size={13}/> FROM YOUR NETWORK</div><h1>Home feed</h1><p>Ideas, work in progress, and conversations from your people.</p></div><Button onClick={() => document.getElementById('post-composer')?.scrollIntoView({ behavior: 'smooth', block: 'center' })}><Send size={14}/> Write a post</Button></div>
    <div className="feed-layout"><main className="feed-main-column"><section id="post-composer" className="surface-card post-composer"><div className="composer-head"><Avatar name={authorName} image={user?.profilePicture}/><textarea aria-label="Create a post" maxLength={5000} value={content} onChange={e => { setContent(e.target.value); setFormError(''); }} onKeyDown={e => { if ((e.metaKey || e.ctrlKey) && e.key === 'Enter') submitPost(); }} placeholder="What are you working on?" rows={3}/></div>{formError && <div className="composer-error">{formError}</div>}{create.isError && <div className="composer-error" role="alert">{getApiErrorMessage(create.error, 'Your post could not be shared.')}</div>}{mediaError && <div className="composer-error">{mediaError}</div>}{mediaPreview && <div className="composer-media-preview">{media?.type.startsWith('video/') ? <video src={mediaPreview} controls/> : <img src={mediaPreview} alt="Post attachment preview"/>}<button type="button" onClick={() => setMedia(null)} aria-label="Remove attachment"><X size={14}/></button></div>}<footer className="composer-footer"><div className="composer-tools"><label className="composer-tool"><input type="file" accept="image/*,video/*" onChange={e => chooseMedia(e.target.files?.[0])}/><ImagePlus size={15}/><span>Photo or video</span></label><label className="composer-visibility"><Globe2 size={13}/><select value={visibility} onChange={e => setVisibility(e.target.value)} aria-label="Post visibility"><option value="PUBLIC">Anyone</option><option value="CONNECTIONS">Connections only</option><option value="PRIVATE">Only me</option></select><ChevronDown size={12}/></label><small className="composer-count">{content.length}/5000</small></div><Button variant="primary" disabled={create.isPending || !content.trim()} onClick={submitPost}>{create.isPending ? <LoaderCircle className="feed-spin" size={14}/> : <Send size={14}/>} {create.isPending ? 'Sharing…' : 'Share post'}</Button></footer></section>
      <div className="feed-stream-heading"><div><h2>Your feed</h2><p>Latest posts from your LinkHub network</p></div><span><ArrowDown size={13}/> Newest first</span></div>
      {postError && <section className="surface-card feed-error" role="alert"><h3>We couldn’t load your feed</h3><p>{postError}</p><Button onClick={() => void feed.refetch()}>Try again</Button></section>}
      {!postError && feed.isLoading && <div className="feed-skeleton-list">{[0, 1, 2].map(i => <FeedSkeleton key={i}/>)}</div>}
      {!postError && !feed.isLoading && posts.length === 0 && <section className="surface-card feed-empty"><span><MessageCircle size={20}/></span><h2>Your feed is ready for its first hello.</h2><p>Share an update or connect with other builders to bring your network into the conversation.</p><Button variant="primary" onClick={() => document.getElementById('post-composer')?.scrollIntoView({ behavior: 'smooth' })}><PlusIcon/> Create a post</Button></section>}
      {!postError && posts.map(post => <PostCard key={post.id} post={post} currentUserId={user?.id} currentUsername={user?.username} currentUserName={authorName}/>)}
      {feed.hasNextPage && <div ref={sentinel} className="feed-sentinel" aria-hidden="true">{feed.isFetchingNextPage && <><LoaderCircle className="feed-spin" size={15}/> Loading more posts…</>}</div>}
      {!postError && posts.length > 0 && !feed.hasNextPage && <div className="feed-end">You’re all caught up.</div>}
      {feed.isFetchNextPageError && <div className="feed-more-error">Couldn’t load more posts. <button onClick={() => void feed.fetchNextPage()}>Try again</button></div>}
    </main><aside className="feed-side-column"><section className="surface-card feed-side-card"><div className="page-kicker"><Code2 size={13}/> YOUR CORNER</div><h3>Good work is better shared.</h3><p>Post a small win, ask a question, or share a project you’re proud of. Your next collaborator might be reading.</p><Link to="/projects/create">Share a project <ArrowUpRight size={13}/></Link></section><section className="feed-guideline"><span><Users size={14}/></span><div><b>Keep it constructive.</b><p>Give credit, be curious, and make room for different points of view.</p></div></section></aside></div>
  </div>;
}

function PlusIcon() { return <span className="feed-plus" aria-hidden="true">+</span>; }
function Avatar({ name, image, compact = false }: { name: string; image?: string | null; compact?: boolean }) { return image ? <img className={`feed-avatar ${compact ? 'compact' : ''}`} src={image} alt={`${name} profile`}/> : <span className={`feed-avatar initials ${compact ? 'compact' : ''}`}>{name.split(/\s+/).slice(0, 2).map(part => part[0]).join('').toUpperCase() || '?'}</span>; }

export function PostCard({ post, currentUserId, currentUsername, currentUserName }: { post: Post; currentUserId?: number; currentUsername?: string; currentUserName: string }) {
  const [commentsOpen, setCommentsOpen] = useState(false);
  const [commentText, setCommentText] = useState('');
  const [likedOverride, setLikedOverride] = useState<boolean | undefined>();
  const [actionError, setActionError] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const commentsQuery = useQuery({ queryKey: ['posts', post.id, 'comments'], queryFn: () => postService.getComments(post.id, 0, 20), enabled: commentsOpen, staleTime: 30_000 });
  const like = usePostLike(); const comment = useCreateComment(post.id); const remove = useDeletePost();
  const author = authorOf(post); const isLiked = likedOverride ?? post.likedByMe ?? false;
  const isMine = (currentUserId != null && (post.userId === currentUserId || post.author?.userId === currentUserId || post.author?.id === currentUserId)) || (!!currentUsername && [post.username, post.author?.username].some(value => value?.toLowerCase() === currentUsername.toLowerCase()));
  const likeCount = post.likeCount ?? post.likesCount;
  const commentCount = commentsQuery.data?.totalElements ?? post.commentCount ?? post.commentsCount;

  async function share() { const url = `${window.location.origin}/home#post-${post.id}`; try { if (navigator.share) await navigator.share({ title: `${author.name} on LinkHub`, text: post.content, url }); else { await navigator.clipboard.writeText(url); setActionError('Post link copied.'); } } catch (error) { if ((error as Error).name !== 'AbortError') setActionError('Could not share this post.'); } }
  function toggleLike() { setActionError(''); like.mutate({ postId: post.id, liked: isLiked }, { onSuccess: result => setLikedOverride(result.liked ?? !isLiked), onError: error => setActionError(getApiErrorMessage(error, 'Could not update your like.')) }); }
  function submitComment() { if (!commentText.trim()) return; comment.mutate(commentText.trim(), { onSuccess: () => setCommentText(''), onError: error => setActionError(getApiErrorMessage(error, 'Could not add your comment.')) }); }

  return <article id={`post-${post.id}`} className="surface-card feed-post"><header className="post-header">{author.userId ? <Link className="post-author-profile" to={`/profile/${author.userId}`} aria-label={`View ${author.name}'s profile`}><Avatar name={author.name} image={author.image}/><div className="post-author"><b>{author.name}</b><span>{author.username ? `@${author.username} · ` : ''}{dateLabel(post.createdAt)}</span></div></Link> : <><Avatar name={author.name} image={author.image}/><div className="post-author"><b>{author.name}</b><span>{author.username ? `@${author.username} · ` : ''}{dateLabel(post.createdAt)}</span></div></>}<div className="post-menu-wrap">{isMine ? <button className="post-menu-button" onClick={() => setMenuOpen(v => !v)} aria-label="Post options"><MoreHorizontal size={17}/></button> : <span className="post-visibility" title={post.visibility === 'CONNECTIONS' ? 'Connections only' : post.visibility === 'PRIVATE' ? 'Only me' : 'Public'}><Globe2 size={13}/></span>}{menuOpen && <div className="post-menu"><button onClick={() => { setMenuOpen(false); setDeleteConfirmOpen(true); setActionError(''); }}><Trash2 size={13}/>Delete post</button></div>}</div></header>
    <div className="post-copy">{post.content}</div>{post.imageUrl && <PostMedia postId={post.id} kind="image" url={post.imageUrl} className="post-media-image"/>}{post.videoUrl && <PostMedia postId={post.id} kind="video" url={post.videoUrl} className="post-media-video"/>}
    {(likeCount != null || commentCount != null || (post.viewCount ?? 0) > 0) && <div className="post-reaction-summary">{likeCount != null ? <span><span className="reaction-heart">♥</span>{likeCount}</span> : <span/>}<button onClick={() => setCommentsOpen(v => !v)}>{commentCount != null ? `${commentCount} ${commentCount === 1 ? 'comment' : 'comments'}` : 'Comments'}</button>{post.viewCount != null && <span className="post-views"><Eye size={12}/>{post.viewCount} views</span>}</div>}
    <div className="post-actions"><button className={isLiked ? 'liked' : ''} onClick={toggleLike} disabled={like.isPending}><ThumbsUp size={15} fill={isLiked ? 'currentColor' : 'none'}/>{isLiked ? 'Liked' : 'Like'}{likeCount != null && <small>{likeCount}</small>}</button><button onClick={() => setCommentsOpen(v => !v)}><MessageCircle size={15}/>Comment{commentCount != null && <small>{commentCount}</small>}</button><button onClick={share}><Share2 size={15}/>Share</button></div>
    {actionError && <div className="post-action-error" role="status">{actionError}<button onClick={() => setActionError('')} aria-label="Dismiss"><X size={12}/></button></div>}
    {commentsOpen && <section className="post-comments"><h3>Comments</h3>{commentsQuery.isLoading && <div className="comments-loading"><LoaderCircle className="feed-spin" size={14}/> Loading comments…</div>}{commentsQuery.isError && <div className="comments-error">{getApiErrorMessage(commentsQuery.error, 'Could not load comments.')} <button onClick={() => void commentsQuery.refetch()}>Retry</button></div>}{commentsQuery.data?.content.map(row => <CommentRow key={row.id} comment={row}/>)}{commentsQuery.data?.content.length === 0 && <p className="comments-empty">No comments yet. Start the conversation.</p>}<div className="comment-composer"><Avatar name={currentUserName} compact/><input value={commentText} maxLength={2000} onChange={e => setCommentText(e.target.value)} onKeyDown={e => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); submitComment(); } }} placeholder="Write a comment…" aria-label="Write a comment"/><button onClick={submitComment} disabled={comment.isPending || !commentText.trim()} aria-label="Send comment">{comment.isPending ? <LoaderCircle className="feed-spin" size={14}/> : <Send size={14}/>}</button></div>{comment.isError && <div className="comments-error">{getApiErrorMessage(comment.error, 'Could not add comment.')}</div>}</section>}
    {deleteConfirmOpen && <PostDeleteConfirm pending={remove.isPending} error={actionError} onCancel={() => setDeleteConfirmOpen(false)} onConfirm={() => remove.mutate(post.id, { onSuccess: () => setDeleteConfirmOpen(false), onError: error => setActionError(getApiErrorMessage(error, 'Could not delete post.')) })}/>}
  </article>;
}

function PostMedia({ postId, kind, url, className }: { postId: number; kind: 'image' | 'video'; url: string; className: string }) {
  const [source, setSource] = useState('');
  useEffect(() => {
    let active = true;
    let objectUrl: string | undefined;
    if (!url.includes('/uploads/')) { setSource(url); return () => { active = false; }; }
    postService.getMedia(postId, kind).then(blob => {
      if (!active) return;
      objectUrl = URL.createObjectURL(blob);
      setSource(objectUrl);
    }).catch(() => { if (active) setSource(''); });
    return () => { active = false; if (objectUrl) URL.revokeObjectURL(objectUrl); };
  }, [kind, postId, url]);
  if (!source) return null;
  return kind === 'image'
    ? <img className={className} src={source} alt="Post attachment" loading="lazy"/>
    : <video className={className} src={source} controls preload="metadata"/>;
}

function CommentRow({ comment }: { comment: Comment }) { const name = [comment.firstName, comment.username].filter(Boolean).join(' ') || 'LinkHub member'; return <div className="comment-row">{comment.userId ? <Link to={`/profile/${comment.userId}`} aria-label={`View ${name}'s profile`}><Avatar name={name} compact/></Link> : <Avatar name={name} compact/>}<div className="comment-bubble"><b>{comment.userId ? <Link to={`/profile/${comment.userId}`}>{name}</Link> : name}</b><small>{dateLabel(comment.createdAt)}</small><p>{comment.content}</p></div></div>; }
function FeedSkeleton() { return <div className="surface-card feed-skeleton"><i/><div><i/><i/></div><i className="wide"/><i className="short"/></div>; }
function PostDeleteConfirm({ pending, error, onCancel, onConfirm }: { pending: boolean; error: string; onCancel: () => void; onConfirm: () => void }) { return <div className="feed-modal-scrim" role="presentation"><section className="surface-card feed-delete-confirm" role="alertdialog" aria-modal="true" aria-labelledby="feed-delete-title"><button className="feed-modal-close" onClick={onCancel} aria-label="Close"><X size={15}/></button><span className="feed-delete-icon"><Trash2 size={17}/></span><h2 id="feed-delete-title">Delete this post?</h2><p>This post will be removed from your feed. This can’t be undone.</p>{error && <div className="feed-error-inline">{error}</div>}<div className="feed-delete-actions"><Button onClick={onCancel}>Keep post</Button><Button className="feed-delete-button" disabled={pending} onClick={onConfirm}>{pending ? 'Deleting…' : 'Delete post'}</Button></div></section></div>; }
