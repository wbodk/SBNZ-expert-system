# SBNZ — Sistem za upravljanje IT infrastrukturom i detekciju incidenata

Pravila-bazirani sistem (Drools) koji klasifikuje sirove metrike i događaje
kroz **4 nivoa forward chaining-a** — od pojedinačnih anomalija (Nivo 1) do
finalnog Alert-a i automatske akcije (Nivo 4) — uz **CEP** i **accumulate** za
vremenske obrasce, **backward chaining** za dijagnozu, i **XLS decision tables**
za politike po klasi imovine. Pune specifikacije su u
[`spec_latest.md`](spec_latest.md), a plan rada u [`PLAN.md`](PLAN.md).

## Struktura

```
incident-detection-app/        Maven multi-modul (Spring Boot 2.7, Drools 7.49, Java 11+)
├── model/      POJO činjenice i događaji (com.ftn.sbnz.model)
├── rules/      DRL pravila + XLS tabele + kmodule.xml
│   └── resources/rules/
│       ├── security/        rules.security        — Minakov (CEP kill chain)
│       ├── infrastructure/  rules.infrastructure  — Avanesov (UBA, accumulate)
│       ├── diagnostics/     rules.diagnostics     — backward chaining (Q1–Q8)
│       └── templates/       rules.templates       — XLS decision tables
└── service/    Spring Boot REST servis + JUnit testovi + scenario runner
client/                        Angular 18 SPA (dashboard)
scripts/                       build.sh / test.sh / run.sh
```

## Pokretanje

### Backend (Spring Boot + Drools)

```bash
./scripts/build.sh   # mvn clean install (skipTests)
./scripts/test.sh    # 30 JUnit testova
./scripts/run.sh     # REST servis na http://localhost:8080
```

Skripte automatski detektuju Java 21/17/11 preko `/usr/libexec/java_home`.

### Klijent (Angular)

```bash
cd client
npm install
npm start            # dev server na http://localhost:4200
```

Backend mora biti pokrenut (CORS je dozvoljen za `localhost:4200`). U klijentu:
izaberi scenario → **Pokreni** → pregledaj alerte, severity, automatske akcije i
backward-chaining dijagnozu; ili koristi **ručni unos** (host + metrika → Fire).

## Baza znanja — 4 nivoa + napredne tehnike

| Nivo | Opis | Primeri pravila |
|---|---|---|
| 1 — Klasifikacija | sirova metrika/event → anomalija | R1.1–R1.14 (HighCPU, BruteForceAttempt, OutboundSpike, SensitiveDataAccess, …) |
| 2 — Korelacija | kombinovanje anomalija | R2.1–R2.10 (ResourceExhaustion, ActiveAttack, SuspiciousDataMovement, …) |
| 3 — Vremenski obrasci | accumulate + CEP | R3.1–R3.13 (BruteForcePattern, DDoSPattern, CompromisedAccount, DataExfiltration, C2Beaconing, InsiderThreat, …) |
| 4 — Odluka i odgovor | severity → Alert + akcija | R4.1–R4.14 (LOW/MEDIUM/HIGH/CRITICAL, isolateHost, revokeAccess, Tier multiplier, compliance) |

**Napredne tehnike po članu tima:**

| Član | Tehnike | Realizacija |
|---|---|---|
| **Roman Minakov** (SV83/2023) | **CEP + Backward chaining** | kill-chain CEP (R3.4–R3.10), stablo zavisnosti + rekurzivni query-ji Q1–Q8 (`rules.diagnostics`) |
| **Avanesov Roman** (SV88/2024) | **Templates + CEP** | XLS decision tables (`rules.templates`), UBA accumulate/CEP (R3.11–R3.13) |

Forward chaining (4 nivoa) i accumulate su zajednička osnova. Severity se po
Tier-u imovine množi iz `AssetCriticality.xls` (×1.5 / ×1.0 / ×0.7) — tačno
jednom (marker `SeverityApplied`).

### Backward chaining (dijagnoza)

Rekurzivni query `isSatisfied(node, host)` prolazi stablo `IncidentDependency`
do listova `IncidentFactor`. Wrapper-i: `isSystemAtRisk` (Q1),
`hasPerformanceDegradation` (Q2), `hasSecurityThreat` (Q3),
`hasAvailabilityIssue` (Q4), `hasDataBreachRisk` (Q5), `hasInsiderThreat` (Q6),
`hasRansomwareRisk` (Q7), te **`whyNotTriggered`** (Q8) — vraća uslove koji su
nedostajali da se incident pokrene.

### XLS decision tables (`rules/templates/`)

- **AssetCriticality.xls** — Tier → severity multiplier + routing (R4.12/R4.13)
- **PerServiceThresholds.xls** — per-klasa CPU prag → `PerfThresholdBreach`
- **CompliancePolicy.xls** — compliance tag (PCI/HIPAA/SOX) → notifikacija (R4.14)

> Binarni `.xls` se generišu Apache POI-jem (`DecisionTableGeneratorTest`,
> idempotentno). Za regenerisanje obrisati fajlove u `rules/templates/` i
> pokrenuti taj test.

## REST API

| Metoda i putanja | Opis |
|---|---|
| `GET  /api/scenarios` | lista demonstracionih scenarija |
| `POST /api/scenarios/{id}/run` | pokreni scenario (A/B) i vrati alerte + dijagnozu |
| `POST /api/fire` | pokreni `fireAllRules` nad tekućim stanjem |
| `POST /api/reset` | rekreiraj sesiju (očisti stanje) |
| `POST /api/clock/advance` | pomeri pseudo-sat (`{amount, unit}`) |
| `POST /api/hosts`, `/api/config/*` | dodaj konfiguraciju (host, baseline, blacklist, profili…) |
| `POST /api/events/{metric\|login\|resource-access\|user-activity\|file\|network}` | ubaci event |
| `GET  /api/alerts`, `/api/severities`, `/api/response-actions`, `/api/state` | tekuće stanje |
| `GET  /api/diagnose/{host}` | Q1–Q7 (backward chaining) |
| `GET  /api/why-not?type=&host=` | Q8 why-not |

Sesija je **jedna deljena `KieSession`** (stream mode, pseudo-clock). Insert ne
pokreće pravila automatski — pozvati `POST /api/fire` (scenario runner ubaci sve
činjenice pa jednom okine, tako da se Tier-multiplikator primeni na finalni severity).

## Scenariji (spec §2)

| Scenario | Host | Očekivano |
|---|---|---|
| **A — DDoS + Compromised Account** | web-01 (TIER_1) | brute force + blacklist login + DDoS obrazac → **CRITICAL**, `lockAccount`/`isolateHost` |
| **B — Insider Threat + Data Exfiltration** | fs-01 (TIER_1, PCI) | UBA anomalija + pristup PCI podacima + outbound spike → **CRITICAL (severity 315)**, `revokeAccess`/`blockOutboundTraffic`/`notifyCompliance_PCI` |

## Testovi (30)

- `InfrastructureRulesTest`, `SecurityRulesTest` — forward chaining (Nivo 1–4), pozitivni + negativni (granice, CEP prozor, accumulate prag)
- `BackwardChainingTest` — rekurzivni query-ji Q1/Q3/Q8
- `DecisionTableRulesTest` — XLS multiplier, per-service prag, compliance
- `ScenarioServiceTest` — end-to-end scenariji A i B
- `DecisionTableGeneratorTest` — generiše XLS tabele

## Pravila po članu tima

| Vlasnik | Paket | Pravila |
|---|---|---|
| Roman Minakov (SV83/2023) — Security & Diagnostics | `rules.security`, `rules.diagnostics` | R1.6–R1.8, R2.3/R2.4/R2.6, R3.1/R3.4–R3.10, R4.6–R4.11, Q1–Q8 (BC) |
| Avanesov Roman (SV88/2024) — Infrastructure & UBA | `rules.infrastructure`, `rules.templates` | R1.1–R1.5/R1.9–R1.14, R2.1/R2.2/R2.5/R2.7–R2.10, R3.2/R3.3/R3.11–R3.13, R4.1–R4.5, R4.12–R4.14 (XLS) |
