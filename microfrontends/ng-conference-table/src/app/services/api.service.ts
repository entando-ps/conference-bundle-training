import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Conference } from '../models/conference.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  public url!: string;
  constructor(private http: HttpClient) { }

  public getAllConferences(): Observable<Conference[]> {
    return this.http.get<Conference[]>(`${this.url}/api/conferences`);
  }
}