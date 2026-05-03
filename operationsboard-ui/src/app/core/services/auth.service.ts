import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { UserDto } from '../models/api.models';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = '/api/auth';

  currentUser = signal<UserDto | null>(null);

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<{ message: string }> {
    const body = new URLSearchParams();
    body.set('username', username);
    body.set('password', password);

    return this.http.post<{ message: string }>(`${this.baseUrl}/login`, body.toString(), {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
      }),
      withCredentials: true,
    });
  }

  loadCurrentUser(): Observable<UserDto> {
    return this.http
      .get<UserDto>(`${this.baseUrl}/me`, {
        withCredentials: true,
      })
      .pipe(tap((user) => this.currentUser.set(user)));
  }

  logout(): Observable<{ message: string }> {
    return this.http
      .post<{ message: string }>(`${this.baseUrl}/logout`, {}, { withCredentials: true })
      .pipe(tap(() => this.currentUser.set(null)));
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }
}
