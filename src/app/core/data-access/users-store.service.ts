import { Injectable, inject, signal } from '@angular/core';
import { NewsfeedApiService } from './newsfeed-api.service';
import type { User } from './newsfeed.models';

@Injectable({ providedIn: 'root' })
export class UsersStore {
  private readonly api = inject(NewsfeedApiService);
  private readonly usersState = signal<User[]>([]);
  private readonly loadingState = signal(false);
  private readonly errorState = signal<string | null>(null);
  private loaded = false;
  private inFlight: Promise<void> | null = null;

  readonly users = this.usersState.asReadonly();
  readonly isLoading = this.loadingState.asReadonly();
  readonly error = this.errorState.asReadonly();

  load(forceRefresh = false): Promise<void> {
    if (this.loaded && !forceRefresh) return Promise.resolve();
    if (this.inFlight) return this.inFlight;

    this.loadingState.set(true);
    this.errorState.set(null);

    this.inFlight = this.api
      .getUsers()
      .then((response) => {
        this.usersState.set(response.users);
        this.loaded = true;
      })
      .catch((error: unknown) => {
        this.errorState.set(error instanceof Error ? error.message : String(error));
      })
      .finally(() => {
        this.loadingState.set(false);
        this.inFlight = null;
      });

    return this.inFlight;
  }
}
