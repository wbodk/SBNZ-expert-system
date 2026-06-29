// Tipovi koji odgovaraju REST odgovorima incident-detection servisa.

export type Level = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Alert {
  hostId: string;
  level: Level;
  severity: number;
  description: string;
}

export interface IncidentSeverity {
  hostId: string;
  score: number;
}

export interface ResponseAction {
  hostId: string;
  action: string;
  detail: string;
}

export interface IncidentFactor {
  id: string;
  hostId: string;
  satisfied: boolean;
  confidence: number;
}

export interface ScenarioInfo {
  id: string;
  name: string;
  description: string;
}

export type Diagnosis = { [query: string]: boolean };

export interface HostDiagnosis {
  queries: Diagnosis;
  whyNotRansomware: IncidentFactor[];
}

export interface RunResult {
  scenarioId: string;
  scenarioName: string;
  focusHosts: string[];
  firedRules: number;
  alerts: Alert[];
  severities: IncidentSeverity[];
  responseActions: ResponseAction[];
  clock: number;
  diagnosis: { [host: string]: HostDiagnosis };
  factsByType: { [type: string]: any[] };
}

export interface EngineState {
  alerts: Alert[];
  severities: IncidentSeverity[];
  responseActions: ResponseAction[];
  clock: number;
  firedRules?: number;
  factsByType?: { [type: string]: any[] };
}
