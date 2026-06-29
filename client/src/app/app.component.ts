import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncidentService } from './incident.service';
import {
  Alert, EngineState, HostDiagnosis, IncidentFactor, IncidentSeverity,
  ResponseAction, RunResult, ScenarioInfo
} from './models';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  scenarios: ScenarioInfo[] = [];
  selectedScenario = 'A';

  runResult: RunResult | null = null;
  state: EngineState | null = null;
  loading = false;
  error: string | null = null;

  // ručni unos
  manualHost = { id: 'srv-01', hostname: 'srv-01.example.com', tier: 'TIER_1', serviceClass: 'web', complianceTag: '' };
  manualMetric = { hostId: 'srv-01', metricType: 'CPU_USAGE', value: 97 };
  metricTypes = ['CPU_USAGE', 'MEMORY_USAGE', 'DISK_USAGE', 'NETWORK_TRAFFIC_MBPS',
    'OUTBOUND_TRAFFIC_MBPS', 'SERVICE_AVAILABILITY', 'SERVICE_RESPONSE_TIME_MS', 'DISK_IO_LATENCY_MS'];
  tiers = ['TIER_1', 'TIER_2', 'TIER_3'];
  serviceClasses = ['web', 'db', 'cache', 'batch'];
  complianceTags = [
    { value: '', label: '(bez compliance)' },
    { value: 'PCI', label: 'PCI' },
    { value: 'HIPAA', label: 'HIPAA' },
    { value: 'SOX', label: 'SOX' }
  ];

  // dijagnoza (ručni mod)
  diagnoseHost = 'srv-01';
  manualDiagnosis: { [q: string]: boolean } | null = null;
  manualWhyNot: IncidentFactor[] = [];

  readonly queryLabels: { [k: string]: string } = {
    isSystemAtRisk: 'Sistem ugrožen (Q1)',
    hasPerformanceDegradation: 'Pad performansi (Q2)',
    hasSecurityThreat: 'Bezbednosna pretnja (Q3)',
    hasAvailabilityIssue: 'Problem dostupnosti (Q4)',
    hasDataBreachRisk: 'Rizik curenja podataka (Q5)',
    hasInsiderThreat: 'Insider pretnja (Q6)',
    hasRansomwareRisk: 'Rizik ransomware-a (Q7)'
  };

  constructor(private api: IncidentService) {}

  ngOnInit(): void {
    this.api.listScenarios().subscribe({
      next: s => this.scenarios = s,
      error: e => this.error = this.fmtErr(e)
    });
  }

  get selectedInfo(): ScenarioInfo | undefined {
    return this.scenarios.find(s => s.id === this.selectedScenario);
  }

  get alerts(): Alert[] {
    return this.runResult?.alerts ?? this.state?.alerts ?? [];
  }
  get severities(): IncidentSeverity[] {
    return this.runResult?.severities ?? this.state?.severities ?? [];
  }
  get responseActions(): ResponseAction[] {
    return this.runResult?.responseActions ?? this.state?.responseActions ?? [];
  }
  get diagnosisHosts(): string[] {
    return this.runResult ? Object.keys(this.runResult.diagnosis) : [];
  }
  hostDiagnosis(host: string): HostDiagnosis | undefined {
    return this.runResult?.diagnosis[host];
  }
  queryEntries(d: { [q: string]: boolean }): { key: string, value: boolean }[] {
    return Object.keys(d).map(k => ({ key: k, value: d[k] }));
  }

  // ---- akcije ----
  runScenario(): void {
    this.busy();
    this.api.runScenario(this.selectedScenario).subscribe({
      next: r => { this.runResult = r; this.state = null; this.loading = false; },
      error: e => this.fail(e)
    });
  }

  reset(): void {
    this.busy();
    this.api.reset().subscribe({
      next: s => { this.runResult = null; this.state = s; this.manualDiagnosis = null; this.manualWhyNot = []; this.loading = false; },
      error: e => this.fail(e)
    });
  }

  fire(): void {
    this.busy();
    this.api.fire().subscribe({
      next: s => { this.state = s; this.runResult = null; this.loading = false; },
      error: e => this.fail(e)
    });
  }

  addHost(): void {
    this.busy();
    const h = { ...this.manualHost, complianceTag: this.manualHost.complianceTag || null };
    this.api.addHost(h).subscribe({
      next: s => { this.state = s; this.runResult = null; this.loading = false; },
      error: e => this.fail(e)
    });
  }

  addMetric(): void {
    this.busy();
    this.api.addMetric(this.manualMetric).subscribe({
      next: s => { this.state = s; this.runResult = null; this.loading = false; },
      error: e => this.fail(e)
    });
  }

  doDiagnose(): void {
    this.busy();
    this.api.diagnose(this.diagnoseHost).subscribe({
      next: d => {
        this.manualDiagnosis = d;
        this.loading = false;
        this.api.whyNot('ransomwareRisk', this.diagnoseHost).subscribe({
          next: w => this.manualWhyNot = w,
          error: () => this.manualWhyNot = []
        });
      },
      error: e => this.fail(e)
    });
  }

  // ---- helpers ----
  levelClass(level: string): string {
    return 'badge badge-' + level.toLowerCase();
  }
  severityPct(score: number): number {
    return Math.min(100, Math.round(score / 3));
  }
  private busy(): void { this.loading = true; this.error = null; }
  private fail(e: any): void { this.error = this.fmtErr(e); this.loading = false; }
  private fmtErr(e: any): string {
    return e?.status === 0
      ? 'Nije moguće povezati se sa REST servisom (/api). Da li je backend pokrenut?'
      : (e?.message ?? 'Greška');
  }
}
