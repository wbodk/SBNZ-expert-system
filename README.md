# SBNZ — Sistem za upravljanje IT infrastrukturom i detekciju incidenata

Pravila-bazirani sistem (Drools) koji klasifikuje sirove metrike i događaje
kroz **4 nivoa forward chaining-a** — od pojedinačnih anomalija (Nivo 1) do
finalnog Alert-a (Nivo 4) — sa CEP i accumulate tehnikama za vremenske
obrasce. Pune specifikacije su u [`spec_latest.md`](spec_latest.md).

## Struktura

```
incident-detection-app/
├── model/      POJO činjenice i događaji (com.ftn.sbnz.model)
├── rules/      DRL pravila + kmodule.xml (Drools knowledge module)
└── service/    Spring Boot servis + JUnit testovi
scripts/        build.sh / test.sh / run.sh
```

## Pokretanje

```bash
./scripts/build.sh   # mvn clean install (skipTests)
./scripts/test.sh    # 18 JUnit testova
./scripts/run.sh     # Spring Boot servis
```

Skripte automatski detektuju Java 21/17/11 preko `/usr/libexec/java_home`.

## Klasni dijagram modela

```mermaid
classDiagram
    direction TB

    %% ---------- Enumeracije ----------
    class Tier {
        <<enumeration>>
        TIER_1
        TIER_2
        TIER_3
    }
    class MetricType {
        <<enumeration>>
        CPU_USAGE
        MEMORY_USAGE
        DISK_USAGE
        NETWORK_TRAFFIC_MBPS
        SERVICE_AVAILABILITY
        SERVICE_RESPONSE_TIME_MS
    }
    class Level {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
        CRITICAL
    }

    %% ---------- Konfiguracija ----------
    class Host {
        +String id
        +String hostname
        +Tier tier
    }

    %% ---------- Ulazni događaji ----------
    class MetricEvent {
        <<event>>
        +String hostId
        +MetricType metricType
        +double value
        +Date timestamp
    }
    class LoginAttemptEvent {
        <<event>>
        +String hostId
        +String userId
        +String sourceIp
        +boolean success
        +Date timestamp
    }

    %% ---------- Nivo 1: Klasifikacija ----------
    class HighCPU {
        +String hostId
        +double value
    }
    class HighMemory {
        +String hostId
        +double value
    }
    class ServiceDown {
        +String hostId
    }
    class TrafficSpike {
        +String hostId
        +double currentMbps
        +double baselineMbps
    }
    class BruteForceAttempt {
        +String hostId
        +long failedCount
    }

    %% ---------- Nivo 2: Korelacija ----------
    class ResourceExhaustion {
        +String hostId
    }
    class PotentialDDoS {
        +String hostId
    }

    %% ---------- Nivo 3: Vremenski obrasci ----------
    class SustainedHighLoad {
        +String hostId
        +double avgValue
    }
    class DDoSPattern {
        +String hostId
    }

    %% ---------- Nivo 4: Odluka ----------
    class IncidentSeverity {
        +String hostId
        +int score
        +add(int delta)
    }
    class Alert {
        +String hostId
        +Level level
        +int severity
        +String description
    }

    %% ---------- Asocijacije sa enumeracijama ----------
    Host --> Tier
    MetricEvent --> MetricType
    Alert --> Level

    %% ---------- Tok pravila (forward chaining) ----------
    MetricEvent ..> HighCPU : R1.1
    MetricEvent ..> HighMemory : R1.2
    LoginAttemptEvent ..> BruteForceAttempt : R1.6 accumulate

    HighCPU ..> ResourceExhaustion : R2.1
    HighMemory ..> ResourceExhaustion : R2.1
    MetricEvent ..> PotentialDDoS : R2.4
    MetricEvent ..> TrafficSpike : R2.4
    MetricEvent ..> ServiceDown : R2.4

    MetricEvent ..> SustainedHighLoad : R3.2 accumulate
    MetricEvent ..> DDoSPattern : R3.4 CEP

    PotentialDDoS ..> IncidentSeverity : +30
    DDoSPattern ..> IncidentSeverity : +45
    ResourceExhaustion ..> IncidentSeverity : +20
    SustainedHighLoad ..> IncidentSeverity : +20

    IncidentSeverity ..> Alert : R4.4 (MEDIUM) / R4.6 (CRITICAL)
```

**Legenda:**
- `<<event>>` — Drools događaj (`@Role(EVENT)`), učestvuje u CEP/accumulate sa `@Timestamp`
- Pune strelice (`-->`) — kompozicija (referenca na enumeraciju)
- Isprekidane strelice (`..>`) — derivacija kroz pravilo (forward chaining); oznaka je broj pravila iz [`spec_latest.md`](spec_latest.md)
- Sve činjenice referenciraju `Host` preko polja `String hostId`

## Pravila po članu tima

| Vlasnik | Paket | Pravila |
|---|---|---|
| Roman Minakov (SV83/2023) — Security | `rules.security` | R1.6, R2.4, R3.4 (CEP), R4.6 |
| Avanesov Roman (SV88/2024) — Infrastructure | `rules.infrastructure` | R1.1, R1.2, R2.1, R3.2 (accumulate), R4.4 |

Svaki vlasnik ima 4–5 pravila koja pokrivaju sva 4 nivoa + jednu naprednu
tehniku iz svoje varijante.
