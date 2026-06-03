import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import type { PostsResponse, UsersResponse } from './newsfeed.models';

const API_BASE_URL = 'http://127.0.0.1:8000';

@Injectable({ providedIn: 'root' })
export class NewsfeedApiService {
  private readonly http = inject(HttpClient);

  getUsers(): Promise<UsersResponse> {
    return firstValueFrom(this.http.get<UsersResponse>(`${API_BASE_URL}/users`));
  }

  getPosts(request: { cursor?: string | null; limit?: number }): Promise<PostsResponse> {
    let params = new HttpParams().set('limit', String(request.limit ?? 30));

    if (request.cursor) {
      params = params.set('cursor', request.cursor);
    }

    return firstValueFrom(this.http.get<PostsResponse>(`${API_BASE_URL}/posts`, { params }));
  }
}
