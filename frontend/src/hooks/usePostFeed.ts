import { useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import type { InfiniteData } from '@tanstack/react-query';
import { postService } from '../services/postService';
import type { Comment, PageResult, Post } from '../types/post';

const FEED_SIZE = 10;
export const postFeedKey = ['posts', 'feed'] as const;
export const commentsKey = (postId: number) => ['posts', postId, 'comments'] as const;

export function usePostFeed() {
  return useInfiniteQuery({
    queryKey: postFeedKey,
    initialPageParam: 0,
    queryFn: ({ pageParam }) => postService.getFeed(pageParam, FEED_SIZE),
    getNextPageParam: (lastPage, pages) => {
      if (lastPage.last === true) return undefined;
      if (lastPage.totalPages != null && (lastPage.number ?? pages.length - 1) + 1 >= lastPage.totalPages) return undefined;
      if (lastPage.last === false || lastPage.totalPages != null) return (lastPage.number ?? pages.length - 1) + 1;
      return lastPage.content.length >= FEED_SIZE ? pages.length : undefined;
    },
  });
}

export function useCreatePost() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: postService.create,
    onSuccess: post => {
      client.setQueryData<InfiniteData<PageResult<Post>, number>>(postFeedKey, previous => {
        if (!previous) return previous;
        return { ...previous, pages: previous.pages.map((page, index) => index === 0 ? { ...page, content: [post, ...page.content] } : page) };
      });
      client.invalidateQueries({ queryKey: postFeedKey });
    },
  });
}

export function usePostLike() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: ({ postId, liked }: { postId: number; liked: boolean }) => liked ? postService.unlike(postId) : postService.like(postId),
    onSuccess: (result, variables) => {
      client.setQueryData<InfiniteData<PageResult<Post>, number>>(postFeedKey, previous => {
        if (!previous) return previous;
        return {
          ...previous,
          pages: previous.pages.map(page => ({ ...page, content: page.content.map(post => post.id === variables.postId ? {
            ...post,
            likedByMe: result.liked ?? !variables.liked,
            likeCount: result.likeCount ?? Math.max(0, (post.likeCount ?? post.likesCount ?? 0) + (variables.liked ? -1 : 1)),
          } : post) })),
        };
      });
    },
  });
}

export function useDeletePost() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: postService.remove,
    onSuccess: (_, postId) => client.setQueryData<InfiniteData<PageResult<Post>, number>>(postFeedKey, previous => previous ? ({ ...previous, pages: previous.pages.map(page => ({ ...page, content: page.content.filter(post => post.id !== postId) })) }) : previous),
  });
}

export function useCreateComment(postId: number) {
  const client = useQueryClient();
  return useMutation({
    mutationFn: (content: string) => postService.comment(postId, content),
    onSuccess: (comment: Comment) => {
      client.setQueryData<InfiniteData<PageResult<Post>, number>>(postFeedKey, previous => previous ? ({ ...previous, pages: previous.pages.map(page => ({ ...page, content: page.content.map(post => post.id === postId ? { ...post, commentCount: post.commentCount == null ? undefined : post.commentCount + 1 } : post) })) }) : previous);
      client.invalidateQueries({ queryKey: commentsKey(postId) });
      void comment;
    },
  });
}
