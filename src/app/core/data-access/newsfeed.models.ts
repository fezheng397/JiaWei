export interface UserPublic {
  id: number;
  first_name: string;
  last_name: string;
  email: string;
}

export type Role = 'user' | 'admin' | 'moderator';
export type Status = 'active' | 'inactive' | 'banned' | 'pending';

export interface User extends UserPublic {
  role: Role;
  status: Status;
}

export interface UsersResponse {
  users: User[];
}

export interface Post {
  id: number;
  author_id: number;
  content: string;
  created_at: string;
  author: UserPublic;
}

export interface PostsResponse {
  has_more: boolean;
  next_cursor: string | null;
  posts: Post[];
}
