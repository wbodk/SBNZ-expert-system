# Sisteme Bazirane na Znanju

*Predlog projekta*

## Sistem za upravljanje IT infrastrukturom i detekciju incidenata

---

## 0. Članovi tima

- Roman Minakov SV83/2023
- Avanesov Roman SV88/2024

### 0.1. Raspodela odgovornosti (Varijanta A)

Prema uputstvu, za ocenu 9–10 svaki član tima implementira **2 od 3** napredne tehnike (CEP, templates, backward chaining). Raspodela:

| Član tima | Napredne tehnike | Domen odgovornosti |
|---|---|---|
| **Roman Minakov (SV83/2023)** — *"Security & Diagnostics"* | **CEP + Backward chaining** | Kill chain CEP obrasci, dijagnostičko stablo zavisnosti, security pravila (BruteForce/DDoS/Exfiltration/C2/Ransomware/Lateral) |
| **Avanesov Roman (SV88/2024)** — *"Infrastructure & UBA"* | **Templates + CEP** | Asset criticality XLS decision tables, UBA modul, performance/availability pravila, CEP obrasci u UBA domenu (InsiderThreat) |

Forward chaining (4 nivoa) i accumulate funkcija su zajednička osnova oba člana.

---

## 1. Opis problema

### 1.1. Motivacija

Savremena IT infrastruktura postaje sve kompleksnija — organizacije istovremeno upravljaju stotinama servera, servisa i mrežnih uređaja koji generišu ogromne količine logova, metrika i mrežnog saobraćaja. Sigurnosni incidenti poput DDoS napada, kompromitovanih naloga, **eksfiltracije podataka, ransomware-a i C2 (command & control) komunikacije** mogu naneti ogromnu štetu pre nego što ih operateri ručno otkriju. Sa druge strane, problemi sa performansama (preopterećeni serveri, padovi servisa) direktno utiču na dostupnost poslovnih sistema.

Posebno je opasna kategorija **insider threat-a** i **slow attack-a** koje tradicionalna rešenja ne primećuju — kompromitovani insajder ili napadač koji deluje sporo i raspodeljeno često ostaje neprimećen mesecima. Otkrivanje ovakvih obrazaca zahteva analizu ponašanja korisnika u kontekstu njihovog ličnog baseline-a i poređenje sa peer grupom.

Tradicionalna rešenja koja se oslanjaju na statička pravila sa fiksnim pragovima nisu dovoljna: generišu previše lažnih upozorenja (false positives), ne prepoznaju složene obrasce koji se razvijaju kroz vreme, ne uzimaju u obzir kontekst korisnika i kritičnost imovine (asset criticality), i ne pružaju transparentno obrazloženje zašto je određeni incident detektovan. Operaterima je potreban sistem koji reaguje na vremenske obrasce u realnom vremenu, automatizuje određene odbrambene akcije, prilagođava prioritete u skladu sa kritičnošću pogođenog sistema, i pritom pruža jasno obrazloženje svake odluke.

### 1.2. Pregled problema

Sistem treba da kontinuirano prati IT infrastrukturu organizacije i donosi odluke o prirodi i ozbiljnosti detektovanih anomalija. Ključni izazovi su:

- **Velika količina podataka:** servisi šalju metrike na nivou sekundi, logovi se generišu kontinuirano
- **Složeni vremenski obrasci:** napad se ne može detektovati jednim događajem, već nizom povezanih događaja kroz vreme (CEP)
- **Različite kategorije incidenata:** sigurnosni incidenti, problemi sa performansama, padovi servisa — svaki zahteva drugačiji prioritet i odgovor
- **Kontekstualno ponašanje:** isti događaj može biti benign ili maliciozni u zavisnosti od korisnika, vremena, i kritičnosti pogođenog sistema
- **Spori i raspodeljeni napadi:** brute force iz 20+ IP adresa kroz dane, C2 beaconing sa regularnim intervalima — zahtevaju dugotrajno akumuliranje dokaza
- **Potreba za objašnjivošću:** operater mora razumeti zašto je sistem podigao upozorenje, posebno kada se preduzimaju automatske akcije

Postojeća rešenja poput Splunk-a, Elastic SIEM-a i Datadog-a koriste kombinaciju ML modela i statičkih pravila. ML modeli daju visoku tačnost ali su "crne kutije" bez obrazloženja odluka. Čisto rule-based sistemi su transparentni ali ne prepoznaju složene vremenske obrasce. Naše rešenje kombinuje **forward chaining za hijerarhijsku klasifikaciju incidenata, Complex Event Processing (CEP) i accumulate za detekciju vremenskih obrazaca, backward chaining za dijagnozu uzroka, XLS decision tables za politike po klasama imovine, i UBA modul za detekciju anomalija ponašanja korisnika** — čime se postiže i tačnost i transparentnost.

#### 1.2.1. Pregled postojećih rešenja i literature

**Splunk Enterprise Security** — industrijsko SIEM rešenje koje koristi korelaciona pravila i ML za detekciju pretnji. Nedostatak: pravila su skriptovana u SPL jeziku, nema forward/backward chaining, ograničena transparentnost automatskih odluka. Izvor: splunk.com/en_us/products/enterprise-security.html

**Elastic SIEM (Kibana Security)** — open-source SIEM nad Elasticsearch stackom. Pruža detekciju na osnovu ML anomaly detection i predefinisanih pravila. Nedostatak: pravila nisu bazirana na znanju, teško proširiva bez tehničkog znanja, bez backward chaining dijagnoze. Izvor: elastic.co/security

**Drools CEP za detekciju mreže (Red Hat)** — demonstracija korišćenja Drools CEP engine-a za analizu mrežnog saobraćaja. Najsličnija tehnika implementaciji našem rešenju, ali ograničena na mrežni sloj bez integracije sa sistemskim metrikama, bez UBA komponente i bez backward chaining dijagnoze. Izvor: developers.redhat.com/blog/2018/07/26/detecting-credit-card-fraud-with-red-hat-decision-manager-7

**Exabeam UEBA / Securonix** — komercijalne UEBA platforme za analizu ponašanja korisnika. Koriste ML statističke modele za peer group comparison i risk scoring. Nedostatak: zatvorene "black box" implementacije bez objašnjivosti i bez backward chaining dijagnoze.

**Prednost našeg rešenja:** Industrijska rešenja nisu bazirana na znanju i ne pružaju transparentno obrazloženje odluka. Naš sistem kombinuje četiri nivoa forward chaining-a, CEP i accumulate za složene vremenske obrasce uključujući kill chain (eksfiltracija, C2, ransomware, slow attack), UBA modul za detekciju insider threat-a, XLS decision tables za politike po Tier-u imovine, i backward chaining za rekurzivnu dijagnozu uzroka — uz potpunu objašnjivost svake automatske akcije.

### 1.3. Metodologija rešenja

#### 1.3.1. Ulazi u sistem (input)

| Tip ulaza | Opis |
|---|---|
| Mrežni saobraćaj | Paketi, protokoli, portovi, IP adrese, volumeni (inbound i outbound) |
| Sistemski logovi | Logovi servera, servisa, OS događaji (syslog, Windows Event Log) |
| Metrike servisa | CPU, RAM, disk I/O, dostupnost, latencija, error rate |
| Konfiguracija | Parametri pragova, poznate IP adrese, definisane politike |
| **Asset registry** | **Tier klasifikacija (TIER_1/2/3), compliance tagovi (PCI/HIPAA), business owner** |
| Korisničke akcije | Broj neuspelih prijava, neobični sati pristupa, akcije na sistemu |
| **Korisnički baseline** | **Prosečan broj logina po danu, tipičan obim preuzetih podataka, normalno radno vreme — per user** |
| **Peer grupe** | **Grupisanje korisnika po ulozi/odeljenju za peer-group comparison** |
| **Sensitive data registry** | **Lista fajl-share-ova, DB tabela i endpoint-a označenih kao sensitive** |
| **File system events** | **Stopa modifikacija fajlova, kreiranje/brisanje, ekstenzije (.encrypted, .lock)** |

#### 1.3.2. Izlazi iz sistema (output)

| Tip izlaza | Opis |
|---|---|
| Alert (upozorenje) | Obaveštenje operateru sa opisom incidenta i prioritetom (LOW/MEDIUM/HIGH/CRITICAL) |
| Automatska akcija | Blokiranje IP adrese, gašenje servisa, izolacija hosta, **blokiranje outbound saobraćaja, izolacija deljenih diskova, revoke pristupa korisniku** |
| Izveštaj | Agregiran pregled incidenata po vremenskim periodima |
| Dijagnoza | Obrazloženje koje je pravilo okidač i koji su čvorovi stabla zadovoljeni (backward chaining) |
| **User risk score** | **Brojčana ocena rizika za svakog korisnika sa istorijom doprinosećih događaja** |
| **Why-not obrazloženje** | **Odgovor na pitanje "zašto incident NIJE detektovan na hostu X" — koji uslov nije zadovoljen** |

#### 1.3.3. Baza znanja projekta

Baza znanja sadrži pravila grupisana u **četiri nivoa**, analogno pristupu iz oblasti detekcije prevara, prilagođena IT infrastrukturnom domenu, i dodatno proširena sa dva ortogonalna modula:

- **Nivo 1 — Klasifikacija događaja:** identifikacija osnovnih anomalija (visoko opterećenje, pad servisa, sumnjiv mrežni saobraćaj, neuspele prijave, mass file modification, sensitive data access)
- **Nivo 2 — Korelacija događaja:** kombinovanje anomalija u složenije incidente (ResourceExhaustion, ActiveAttack, PotentialDDoS, SuspiciousDataMovement, PossibleRansomware)
- **Nivo 3 — Vremenski obrasci (accumulate + CEP):** detekcija obrazaca koji se razvijaju kroz vreme (brute force, DDoS, lateral movement, cascade failure, **data exfiltration, C2 beaconing, ransomware activity, slow brute force, insider threat**)
- **Nivo 4 — Odluka i odgovor:** donošenje finalne odluke o prioritetu incidenta i automatske akcije na osnovu severity score-a, uz **multiplier-e prema Tier-u imovine** (XLS decision table)

**Ortogonalni moduli:**

- **UBA modul (User Behavior Analytics):** prati per-user baseline (broj logina, obim preuzetih podataka, radno vreme), poredi sa peer grupom, izračunava per-user risk score, detektuje insider threat. Koristi accumulate za baseline learning i CEP za sekvence sumnjivih akcija.
- **Asset criticality modul:** XLS decision table klasifikuje hostove u Tier 1/2/3 prema business kritičnosti i compliance tagovima; severity score se množi multiplier-om iz tabele.

Baza znanja se popunjava iz: konfiguracionih fajlova organizacije (politike, whitelist/blacklist IP adresa, dozvoljeni sati pristupa, Tier klasifikacija), ekspertskog znanja tima za sigurnost (definicija normalnog ponašanja, pragovi za svaki servis), istorijskih podataka o incidentima i per-user baseline-a (akumulira se kroz vreme). Domenski ekspert za projekat biće sertifikovani sistemski administrator sa iskustvom u incident response-u.

#### 1.3.4. Tehnike implementacije

- **Forward chaining (4 nivoa ulančavanja):** klasifikacija → korelacija → vremenski obrasci → odluka
- **Accumulate funkcija:** broj neuspelih prijava u vremenskom prozoru, prosečno CPU opterećenje, broj padova servisa, broj različitih izvora napada, **broj outbound konekcija po hostu, per-user dnevni baseline, broj jedinstvenih sourceIP-jeva za slow brute force**
- **Complex Event Processing (CEP):**
  - *DDoS pattern:* TrafficSpike + ServiceDown + HighCPU u roku od 2 minuta
  - *Compromised account:* BruteForceAttempt praćen AnomalousAccess u roku od 5 minuta
  - *Lateral movement:* SuspiciousIP detektovan na 3+ različita servisa za manje od 10 minuta
  - **Data exfiltration:** SensitiveDataAccess praćen OutboundSpike u roku od 15 minuta
  - **C2 beaconing:** regularni outbound zahtevi sa konstantnim intervalom (±5%) tokom 30+ minuta
  - **Ransomware activity:** MassFileModification + ServiceDown + HighCPU u roku od 5 minuta
  - **Slow brute force (accumulate, ne CEP):** failedLogins > 20 sa DISTINCT srcIP > 15 u prozoru od 24h
  - **Insider threat (CEP):** UnusualLoginFrequency praćen SensitiveDataAccess u roku od 1h
- **Templates (Drools XLS Decision Tables):**
  - *AssetCriticality.xls:* mapira host pattern → Tier (1/2/3) → severity multiplier (1.5 / 1.0 / 0.7) → alert routing
  - *PerServiceThresholds.xls:* različiti pragovi CPU/RAM/disk za različite klase servisa (web, db, cache, batch)
  - *CompliancePolicy.xls:* PCI/HIPAA/SOX tagovi → dodatne notifikacije i retention politike
  - Programski templates: isti šablon pravila za različite servise i hostove sa različitim konfiguracionim parametrima (pragma CPU, RAM, dozvoljene IP adrese)
- **Backward chaining:** upit "Da li je sistem X trenutno ugrožen?" — rekurzivni query `isSystemAtRisk(hostId)` koji prolazi stablo zavisnosti incidenata. Pored toga: **`whyNotTriggered(incidentType, hostId)`** vraća listu uslova koji su nedostajali da bi se incident pokrenuo (Why-not eksplanacija).

#### 1.3.5. Backward chaining — rekurzivno stablo zavisnosti

Backward chaining se implementira kroz rekurzivni query nad stablom zavisnosti incidenata. Uvode se dva tipa činjenica:

- **IncidentFactor** — konkretna činjenica na listu stabla (npr. "host web-server-01 ima visoko CPU opterećenje"). Sadrži polja: `id (String)`, `hostId (String)`, `satisfied (boolean)`, `confidence (double)`.
- **IncidentDependency** — veza između roditeljskog i dečjeg čvora u stablu (npr. "systemAtRisk zavisi od hasSecurityThreat"). Sadrži polja: `parentId (String)`, `childId (String)`.

Stablo zavisnosti je prošireno na 4 nivoa (umesto plitkog 2-nivoa):

```
isSystemAtRisk(X)
├── hasPerformanceDegradation(X)
│   ├── hasResourceExhaustion(X) → HighCPU, HighMemory
│   ├── hasStorageProblem(X) → HighDisk, SlowDiskIO
│   └── hasSlowResponse(X) → SlowService, HighLatency
├── hasSecurityThreat(X)
│   ├── hasActiveAttack(X)
│   │   ├── hasBruteForce(X) → BruteForceAttempt, BruteForcePattern, SlowBruteForce
│   │   └── hasDDoS(X) → PotentialDDoS, DDoSPattern, DistributedAttack
│   ├── hasCompromise(X)
│   │   ├── hasCompromisedAccount(X) → CompromisedAccount, AnomalousAccess
│   │   └── hasInsiderThreat(X) → InsiderThreat, HighRiskUser
│   ├── hasLateralMovement(X) → LateralMovement, SuspiciousIP
│   └── hasDataBreachRisk(X)
│       ├── hasExfiltration(X) → DataExfiltration, SuspiciousDataMovement
│       └── hasC2Activity(X) → C2Beaconing
├── hasAvailabilityIssue(X)
│   ├── hasServiceDown(X) → ServiceDown
│   └── hasCascadeFailure(X) → CascadeFailure
└── hasRansomwareRisk(X) → RansomwareActivity, PossibleRansomware, MassFileModification
```

Dodatno se uvodi **Why-not query** `whyNotTriggered(incidentType, hostId)` koji vraća skup `IncidentFactor`-a sa `satisfied = false` koji su sprečavali aktivaciju navedenog inicidenta — koristi se za audit i debug.

#### 1.3.6. Kompletna lista pravila u sistemu

##### Nivo 1 — Klasifikacija događaja

| Pravilo | Uslov (WHEN) | Akcija (THEN) |
|---|---|---|
| R1.1 | cpuUsage > 90% for > 5 min | insert(HighCPU) |
| R1.2 | memUsage > 95% | insert(HighMemory) |
| R1.3 | diskUsage > 90% | insert(HighDisk) |
| R1.4 | serviceResponseTime > 3000ms | insert(SlowService) |
| R1.5 | serviceAvailability == DOWN | insert(ServiceDown) |
| R1.6 | failedLoginCount > 5 in 1 min | insert(BruteForceAttempt) |
| R1.7 | sourceIP IN blacklist | insert(SuspiciousIP) |
| R1.8 | networkTrafficVolume > 10x avgBaseline | insert(TrafficSpike) |
| **R1.9** | **outboundTrafficVolume > 5x outboundBaseline** | **insert(OutboundSpike)** |
| **R1.10** | **fileModificationRate > 100 files/min** | **insert(MassFileModification)** |
| **R1.11** | **resourceId IN sensitiveDataRegistry AND accessGranted** | **insert(SensitiveDataAccess)** |
| **R1.12** | **userLoginCount > userBaseline.avgLogins × 3** | **insert(UnusualLoginFrequency)** |
| **R1.13** | **userDataDownloadVolume > userBaseline.avgVolume × 5** | **insert(UnusualDataVolume)** |
| **R1.14** | **diskIOLatency > 500ms for > 3 min** | **insert(SlowDiskIO)** |

##### Nivo 2 — Korelacija događaja

| Pravilo | Uslov (WHEN) | Akcija (THEN) |
|---|---|---|
| R2.1 | HighCPU AND HighMemory | insert(ResourceExhaustion); severity += 20 |
| R2.2 | ServiceDown AND HighDisk | insert(StorageIncident); severity += 25 |
| R2.3 | BruteForceAttempt AND SuspiciousIP | insert(ActiveAttack); severity += 40 |
| R2.4 | SlowService AND TrafficSpike | insert(PotentialDDoS); severity += 30 |
| R2.5 | ServiceDown AND NOT ScheduledMaintenance | severity += 35 |
| R2.6 | loginHour NOT IN allowedHours AND failedLogins > 0 | insert(AnomalousAccess); severity += 20 |
| **R2.7** | **SensitiveDataAccess AND OutboundSpike** | **insert(SuspiciousDataMovement); severity += 35** |
| **R2.8** | **UnusualLoginFrequency AND UnusualDataVolume** | **insert(SuspiciousUserActivity); severity += 25** |
| **R2.9** | **MassFileModification AND ServiceDown** | **insert(PossibleRansomware); severity += 40** |
| **R2.10** | **SensitiveDataAccess AND loginHour NOT IN allowedHours** | **insert(SuspiciousAfterHoursAccess); severity += 30** |

##### Nivo 3 — Vremenski obrasci (accumulate + CEP)

| Pravilo | Tip | Uslov (WHEN) | Akcija (THEN) |
|---|---|---|---|
| R3.1 | accumulate | COUNT(failedLogins) over 10 min > 50 | insert(BruteForcePattern); severity += 30 |
| R3.2 | accumulate | AVG(cpuUsage) over 30 min > 85% | insert(SustainedHighLoad); severity += 20 |
| R3.3 | accumulate | COUNT(ServiceDown) over 5 min > 3 | insert(CascadeFailure); severity += 40 |
| R3.4 | CEP | TrafficSpike + ServiceDown + HighCPU in 2 min | insert(DDoSPattern); severity += 45 |
| R3.5 | CEP | BruteForceAttempt → AnomalousAccess within 5 min | insert(CompromisedAccount); severity += 50 |
| R3.6 | CEP | SuspiciousIP appeared on 3+ different services < 10 min | insert(LateralMovement); severity += 45 |
| R3.7 | accumulate | COUNT(DISTINCT srcIP) over 1 min > 500 | insert(DistributedAttack); severity += 40 |
| **R3.8** | **CEP** | **SensitiveDataAccess → OutboundSpike within 15 min (same host)** | **insert(DataExfiltration); severity += 50** |
| **R3.9** | **CEP** | **outbound connections to same destIP with constant interval ±5% for 30+ min** | **insert(C2Beaconing); severity += 45** |
| **R3.10** | **CEP** | **MassFileModification + ServiceDown + HighCPU within 5 min (same host)** | **insert(RansomwareActivity); severity += 60** |
| **R3.11** | **accumulate** | **COUNT(failedLogins) over 24h > 20 AND COUNT(DISTINCT srcIP) over 24h > 15** | **insert(SlowBruteForce); severity += 35** |
| **R3.12** | **accumulate** | **UserRiskScore.value > peerGroup.avgRisk × 2** | **insert(HighRiskUser); severity += 30** |
| **R3.13** | **CEP** | **UnusualLoginFrequency → SensitiveDataAccess within 1h (same user)** | **insert(InsiderThreat); severity += 40** |
| **R3.14** | **accumulate** | **per-user baseline update: AVG(dailyLogins, dailyDataVolume) over 14 days** | **update(UserActivityProfile)** |

##### Nivo 4 — Odluka i odgovor

| Pravilo | Uslov (WHEN) | Akcija (THEN) |
|---|---|---|
| R4.1 | DDoSPattern AND CascadeFailure | severity += 20 (bonus) |
| R4.2 | CompromisedAccount AND LateralMovement | severity += 30 (bonus) |
| R4.3 | severity < 30 | Alert: LOW; logIncident() |
| R4.4 | severity >= 30 AND severity < 60 | Alert: MEDIUM; notifyOncall() |
| R4.5 | severity >= 60 AND severity < 85 | Alert: HIGH; notifyTeam(); blockSuspiciousIP() |
| R4.6 | severity >= 85 | Alert: CRITICAL; isolateHost(); notifyManagement(); activateIRP() |
| R4.7 | CompromisedAccount exists | forceLogout(); lockAccount(); notifySecOps() |
| **R4.8** | **DataExfiltration exists** | **blockOutboundTraffic(); snapshotEvidence(); notifyDataProtection()** |
| **R4.9** | **RansomwareActivity exists** | **isolateHost(); disableSharedDrives(); activateBackupRestore(); severity += 25** |
| **R4.10** | **InsiderThreat exists** | **revokeAccess(user); notifyHR(); auditTrail()** |
| **R4.11** | **C2Beaconing exists** | **blockDestIP(); deepPacketInspection(); notifyThreatIntel()** |
| **R4.12** | **host.tier == TIER_1 (from XLS)** | **severity *= 1.5; routing: management+oncall** |
| **R4.13** | **host.tier == TIER_3 (from XLS)** | **severity *= 0.7; routing: oncall only** |
| **R4.14** | **host.complianceTag CONTAINS "PCI"** | **notifyCompliance(); retainLogs(7 years)** |

##### Backward chaining query-ji

| Query | Naziv | Opis |
|---|---|---|
| Q1 | isSystemAtRisk(X) | Rekurzivni koren — prolazi stablo zavisnosti incidenata |
| Q2 | hasPerformanceDegradation(X) | Proverava HighCPU, HighMemory, HighDisk, SlowService, SlowDiskIO, HighLatency |
| Q3 | hasSecurityThreat(X) | Proverava ActiveAttack, Compromise, LateralMovement, DataBreachRisk |
| Q4 | hasAvailabilityIssue(X) | Proverava ServiceDown, CascadeFailure |
| **Q5** | **hasDataBreachRisk(X)** | **Proverava DataExfiltration, C2Beaconing, SuspiciousDataMovement** |
| **Q6** | **hasInsiderThreat(X)** | **Proverava InsiderThreat, HighRiskUser, SuspiciousUserActivity** |
| **Q7** | **hasRansomwareRisk(X)** | **Proverava RansomwareActivity, PossibleRansomware** |
| **Q8** | **whyNotTriggered(incidentType, X)** | **Why-not query: vraća IncidentFactor-e sa satisfied=false koji su sprečili aktivaciju** |

---

## 2. Konkretni primeri rezonovanja

### 2.1. Scenarij A — DDoS + Compromised Account (vlasnik: Roman Minakov)

**Infrastruktura organizacije:** web server (web-01), baza podataka (db-01), auth servis (auth-01). web-01 je klasifikovan kao **TIER_1** u AssetCriticality.xls (severity multiplier 1.5).

U periodu od 5 minuta pristižu sledeći događaji:

| № | Vreme | Host | Metrika | Vrednost |
|---|---|---|---|---|
| 1 | 14:00:00 | auth-01 | failedLogins | 55 neuspelih prijava u 10 min |
| 2 | 14:01:30 | web-01 | networkTraffic | 850 Mbps (baseline: 50 Mbps) |
| 3 | 14:02:00 | web-01 | cpuUsage | 97% |
| 4 | 14:02:30 | web-01 | serviceAvailability | DOWN |
| 5 | 14:03:00 | 192.168.1.55 | loginAttempt | Uspešna prijava na web-01 (neobičan sat) |

**Rezonovanje sistema:**

**Nivo 1 — Klasifikacija:**
- R1.6: 55 failedLogins u 10 min → insert(BruteForceAttempt)
- R1.8: networkTraffic 850 Mbps >> 10× baseline (500 Mbps) → insert(TrafficSpike)
- R1.1: cpuUsage 97% > 90% for 5 min → insert(HighCPU)
- R1.5: web-01 serviceAvailability == DOWN → insert(ServiceDown)

**Nivo 2 — Korelacija:**
- R2.4: SlowService AND TrafficSpike → insert(PotentialDDoS); severity += 30
- R2.6: loginHour NOT IN allowedHours AND failedLogins > 0 → insert(AnomalousAccess); severity += 20

**Nivo 3 — CEP i accumulate:**
- R3.1 (accumulate): COUNT(failedLogins) over 10 min = 55 > 50 → insert(BruteForcePattern); severity += 30
- R3.4 (CEP): TrafficSpike + ServiceDown + HighCPU u roku od 2 minuta → insert(DDoSPattern); severity += 45
- R3.5 (CEP): BruteForceAttempt → AnomalousAccess u roku od 5 minuta → insert(CompromisedAccount); severity += 50

**Nivo 4 — Odluka (sa Tier multiplier-om):**
- Bazni severity = 30 + 20 + 30 + 45 + 50 = 175
- R4.12 (XLS): web-01.tier == TIER_1 → severity *= 1.5 → severity = 262 → **CRITICAL**
- R4.6: Alert CRITICAL; isolateHost(web-01); notifyManagement(); activateIRP()
- R4.7: CompromisedAccount → forceLogout(192.168.1.55); lockAccount(); notifySecOps()

**Backward chaining dijagnoza:**
Na upit `isSystemAtRisk("web-01")` sistem prolazi stablo i potvrđuje:
- `systemAtRisk ← hasSecurityThreat ← hasCompromise ← hasCompromisedAccount` (satisfied = true)
- `systemAtRisk ← hasAvailabilityIssue ← hasServiceDown` (satisfied = true)
- `systemAtRisk ← hasPerformanceDegradation ← hasResourceExhaustion ← HighCPU` (satisfied = true)
- `systemAtRisk ← hasSecurityThreat ← hasActiveAttack ← hasDDoS ← DDoSPattern` (satisfied = true)

**Why-not provera:** `whyNotTriggered("RansomwareActivity", "web-01")` → vraća `MassFileModification (satisfied=false)` — objašnjava da nije bio ransomware, jer nije bilo masovne modifikacije fajlova.

Operater dobija potpuno obrazloženje: koji čvorovi stabla su zadovoljeni, koja pravila su ih aktivirala, i zašto određeni inciden_ti_ nisu pokrenuti.

---

### 2.2. Scenarij B — Insider Threat + Data Exfiltration (vlasnik: Avanesov Roman)

**Infrastruktura organizacije:** file server (fs-01) sa sensitive folderom, gde se nalaze PCI-tagovani podaci. Korisnik `jdoe` je standardni zaposleni — njegov UserActivityProfile pokazuje: prosečno 8 logina dnevno, 50 MB preuzetih podataka, radno vreme 09:00–18:00.

U periodu od 90 minuta pristižu sledeći događaji:

| № | Vreme | Host/User | Metrika | Vrednost |
|---|---|---|---|---|
| 1 | 21:30:00 | jdoe | loginCount | 28 logina za dan (baseline avg = 8) |
| 2 | 21:45:00 | fs-01 | resourceAccess | jdoe → /sensitive/pci_dump.csv (sensitive registry) |
| 3 | 21:50:00 | fs-01 | outboundTraffic | 320 MB outbound (baseline: 40 MB) |
| 4 | 22:00:00 | jdoe | dataDownloadVolume | 450 MB za sesiju (baseline avg = 50 MB) |

**Rezonovanje sistema:**

**Nivo 1 — Klasifikacija:**
- R1.12: userLoginCount 28 > userBaseline.avgLogins × 3 (24) → insert(UnusualLoginFrequency)
- R1.11: pci_dump.csv ∈ sensitiveDataRegistry → insert(SensitiveDataAccess)
- R1.9: outboundTraffic 320 MB > 5× baseline (200 MB) → insert(OutboundSpike)
- R1.13: dataDownloadVolume 450 MB > userBaseline.avgVolume × 5 (250 MB) → insert(UnusualDataVolume)

**Nivo 2 — Korelacija:**
- R2.7: SensitiveDataAccess AND OutboundSpike → insert(SuspiciousDataMovement); severity += 35
- R2.8: UnusualLoginFrequency AND UnusualDataVolume → insert(SuspiciousUserActivity); severity += 25
- R2.10: SensitiveDataAccess AND loginHour (21:45) NOT IN allowedHours (09–18) → insert(SuspiciousAfterHoursAccess); severity += 30

**Nivo 3 — CEP i accumulate:**
- R3.12 (accumulate): UserRiskScore za jdoe = 78, peer group avg = 30 → 78 > 30 × 2 → insert(HighRiskUser); severity += 30
- R3.13 (CEP): UnusualLoginFrequency (21:30) → SensitiveDataAccess (21:45) within 1h → insert(InsiderThreat); severity += 40
- R3.8 (CEP): SensitiveDataAccess (21:45) → OutboundSpike (21:50) within 15 min, same host fs-01 → insert(DataExfiltration); severity += 50

**Nivo 4 — Odluka (sa Tier multiplier-om i PCI tagom):**
- Bazni severity = 35 + 25 + 30 + 30 + 40 + 50 = 210
- R4.12 (XLS): fs-01.tier == TIER_1 (sensitive fileserver) → severity *= 1.5 → severity = 315 → **CRITICAL**
- R4.6: Alert CRITICAL; isolateHost(fs-01); notifyManagement(); activateIRP()
- R4.10: InsiderThreat → revokeAccess(jdoe); notifyHR(); auditTrail()
- R4.8: DataExfiltration → blockOutboundTraffic(fs-01); snapshotEvidence(); notifyDataProtection()
- R4.14: fs-01.complianceTag CONTAINS "PCI" → notifyCompliance(); retainLogs(7 years)

**Backward chaining dijagnoza:**
Na upit `isSystemAtRisk("fs-01")` sistem prolazi prošireno stablo i potvrđuje:
- `systemAtRisk ← hasSecurityThreat ← hasDataBreachRisk ← hasExfiltration ← DataExfiltration` (satisfied = true)
- `systemAtRisk ← hasSecurityThreat ← hasCompromise ← hasInsiderThreat ← InsiderThreat` (satisfied = true)
- `systemAtRisk ← hasSecurityThreat ← hasCompromise ← hasInsiderThreat ← HighRiskUser` (satisfied = true)

**Why-not provera:** `whyNotTriggered("RansomwareActivity", "fs-01")` → vraća `MassFileModification (satisfied=false), HighCPU (satisfied=false)` — potvrđuje da napad nije bio ransomware nego eksfiltracija (čitanje, ne pisanje).

**Pregled doprinosa UBA modula:** bez per-user baseline-a i peer-group comparison-a, klasični prag-bazirani sistem ne bi ni primetio jdoe-a kao anomaliju (svi pojedinačni događaji su tehnički "validni" — uspešni logini, autorizovani pristup, normalan saobraćaj po sistemskom baseline-u). UBA modul i kontekstualni Tier/compliance multiplier-i čine ovu detekciju mogućom uz potpunu objašnjivost.

---

## 3. Pregled doprinosa po članovima tima

| Doprinos | Roman Minakov (SV83/2023) | Avanesov Roman (SV88/2024) |
|---|---|---|
| Nivo 1 pravila | R1.6–R1.8 (security) | R1.1–R1.5, R1.9–R1.14 (infrastructure, UBA, data) |
| Nivo 2 pravila | R2.3–R2.4, R2.6 | R2.1–R2.2, R2.5, R2.7–R2.10 |
| Nivo 3 pravila | R3.4–R3.10 (CEP security + kill chain) | R3.1–R3.3, R3.11–R3.14 (accumulate + UBA CEP) |
| Nivo 4 pravila | R4.6–R4.11 (severity + security odgovor) | R4.1–R4.5, R4.12–R4.14 (severity thresholds + Tier/compliance) |
| **CEP** | DDoSPattern, CompromisedAccount, LateralMovement, DataExfiltration, C2Beaconing, RansomwareActivity | InsiderThreat (UBA CEP) |
| **Backward chaining** | Stablo zavisnosti (4 nivoa), Q1–Q7, Why-not (Q8) | — |
| **Templates** | — | AssetCriticality.xls, PerServiceThresholds.xls, CompliancePolicy.xls + programski templates |
| **Accumulate** | BruteForcePattern, DistributedAttack | SustainedHighLoad, CascadeFailure, SlowBruteForce, HighRiskUser, UserActivityProfile update |
| **Konkretni scenarij** | Scenarij A (DDoS + Compromised) | Scenarij B (Insider + Exfiltration) |

Svaki član tima realizuje **2 od 3 napredne tehnike** prema uputstvu za ocenu 9–10:
- Roman Minakov: **CEP + Backward chaining**
- Avanesov Roman: **Templates + CEP**

Forward chaining (4 nivoa) i accumulate funkcija su zajednička osnova oba člana tima.
