import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Alert, Diagnosis, EngineState, IncidentFactor, RunResult, ScenarioInfo
} from './models';

/** Klijent ka REST API-ju incident-detection servisa. */
@Injectable({ providedIn: 'root' })
export class IncidentService {

  // Relativna putanja: u dev-u ide kroz proxy.conf.json, u Docker-u kroz nginx
  // (oba prosleđuju na backend :8080) — pa nema CORS problema.
  private readonly base = '/api';

  constructor(private http: HttpClient) {}

  // --- scenariji ---
  listScenarios(): Observable<ScenarioInfo[]> {
    return this.http.get<ScenarioInfo[]>(`${this.base}/scenarios`);
  }

  runScenario(id: string): Observable<RunResult> {
    return this.http.post<RunResult>(`${this.base}/scenarios/${id}/run`, {});
  }

  // --- sesija ---
  reset(): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/reset`, {});
  }

  fire(): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/fire`, {});
  }

  fullState(): Observable<EngineState> {
    return this.http.get<EngineState>(`${this.base}/state`);
  }

  advanceClock(amount: number, unit: string): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/clock/advance`, { amount, unit });
  }

  // --- ručni unos činjenica ---
  addHost(host: any): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/hosts`, host);
  }

  addMetric(req: any): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/events/metric`, req);
  }

  addLogin(req: any): Observable<EngineState> {
    return this.http.post<EngineState>(`${this.base}/events/login`, req);
  }

  // --- upiti / dijagnoza ---
  alerts(): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${this.base}/alerts`);
  }

  diagnose(host: string): Observable<Diagnosis> {
    return this.http.get<Diagnosis>(`${this.base}/diagnose/${host}`);
  }

  whyNot(type: string, host: string): Observable<IncidentFactor[]> {
    return this.http.get<IncidentFactor[]>(
      `${this.base}/why-not?type=${encodeURIComponent(type)}&host=${encodeURIComponent(host)}`);
  }
}
