export interface PostAuthor {
  id?: number;
  userId?: number;
  username?: string;
  fullName?: string;
  firstName?: string;
  lastName?: string;
  profilePicture?: string | null;
}

export interface Post {
  id: number;
  content: string;
  imageUrl?: string | null;
  videoUrl?: string | null;
  visibility?: string;
  viewCount?: number;
  likeCount?: number;
  likesCount?: number;
  commentCount?: number;
  commentsCount?: number;
  likedByMe?: boolean;
  userId?: number;
  username?: string;
  fullName?: string;
  author?: PostAuthor;
  createdAt?: string;
}

export interface Comment {
  id: number;
  content: string;
  postId: number;
  userId?: number;
  username?: string;
  firstName?: string;
  createdAt?: string;
}

export interface PageResult<T> {
  content: T[];
  number?: number;
  totalPages?: number;
  totalElements?: number;
  last?: boolean;
  first?: boolean;
  size?: number;
}
