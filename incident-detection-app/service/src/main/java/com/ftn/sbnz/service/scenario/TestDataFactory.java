package com.ftn.sbnz.service.scenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ftn.sbnz.model.BlacklistedIp;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.Host.Tier;
import com.ftn.sbnz.model.HostBaseline;
import com.ftn.sbnz.model.LoginAttemptEvent;
import com.ftn.sbnz.model.MetricEvent;
import com.ftn.sbnz.model.MetricEvent.MetricType;
import com.ftn.sbnz.model.PeerGroupStats;
import com.ftn.sbnz.model.ResourceAccessEvent;
import com.ftn.sbnz.model.SensitiveResource;
import com.ftn.sbnz.model.UserActivityEvent;
import com.ftn.sbnz.model.UserActivityProfile;
import com.ftn.sbnz.model.UserRiskScore;

/**
 * Java fixtures za demonstracione scenarije (spec_latest.md §2). Eventi nose
 * timestamp-ove relativno na prosleđeni {@code t0} (vreme pseudo-sata), pa ih
 * {@link ScenarioService} reprodukuje deterministički.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    /** Pregled dostupnih scenarija za REST listu. */
    public static List<ScenarioInfo> list() {
        return Arrays.asList(
            new ScenarioInfo("A", "DDoS + Compromised Account",
                "web-01 (TIER_1): brute force + blacklisted login (kompromitovan nalog) "
                + "uz DDoS obrazac (TrafficSpike + ServiceDown + HighCPU). Očekivano: CRITICAL."),
            new ScenarioInfo("B", "Insider Threat + Data Exfiltration",
                "fs-01 (TIER_1, PCI): neuobičajena aktivnost korisnika jdoe, pristup PCI podacima "
                + "van radnog vremena i outbound spike. Očekivano: CRITICAL + exfiltration/insider.")
        );
    }

    public static ScenarioData byId(String id, long t0) {
        if ("A".equalsIgnoreCase(id)) {
            return scenarioA(t0);
        }
        if ("B".equalsIgnoreCase(id)) {
            return scenarioB(t0);
        }
        throw new IllegalArgumentException("Nepoznat scenario: " + id);
    }

    // =================================================================
    // Scenarij A — DDoS + Compromised Account (vlasnik: Minakov)
    // =================================================================
    public static ScenarioData scenarioA(long t0) {
        ScenarioData d = new ScenarioData("A", "DDoS + Compromised Account");
        d.focusHosts.add("web-01");

        // --- konfiguracija ---
        d.setup.add(new Host("web-01", "web-01.example.com", Tier.TIER_1, "web", null));
        d.setup.add(new HostBaseline("web-01", 50.0, 40.0));     // 10× = 500 Mbps
        d.setup.add(new BlacklistedIp("192.168.1.55"));
        d.setup.add(new UserActivityProfile("admin", 8, 50.0, 9, 18, "ops"));

        // --- eventi ---
        // 54 neuspele prijave sa 20 različitih IP (sat 03h) u ~108s:
        //   R1.6 BruteForceAttempt, R3.1 BruteForcePattern (>50), R3.11 SlowBruteForce (>20 fail, >15 IP)
        for (int i = 0; i < 54; i++) {
            d.events.add(new LoginAttemptEvent("web-01", "admin", "10.0.0." + (i % 20 + 1),
                    false, 3, ts(t0, i * 2)));
        }
        // uspešna prijava sa blacklist IP u 03h -> SuspiciousIP + (kasnije) AnomalousAccess
        d.events.add(new LoginAttemptEvent("web-01", "attacker", "192.168.1.55", true, 3, ts(t0, 120)));

        // DDoS komponente u prozoru od 2 min (R3.4) + SlowService (R2.4) + perf
        d.events.add(new MetricEvent("web-01", MetricType.NETWORK_TRAFFIC_MBPS, 850.0, ts(t0, 130)));
        d.events.add(new MetricEvent("web-01", MetricType.SERVICE_RESPONSE_TIME_MS, 5000.0, ts(t0, 135)));
        d.events.add(new MetricEvent("web-01", MetricType.CPU_USAGE, 97.0, ts(t0, 140)));
        d.events.add(new MetricEvent("web-01", MetricType.SERVICE_AVAILABILITY, 0.0, ts(t0, 150)));

        d.spanSeconds = 150;
        return d;
    }

    // =================================================================
    // Scenarij B — Insider Threat + Data Exfiltration (vlasnik: Avanesov)
    // =================================================================
    public static ScenarioData scenarioB(long t0) {
        ScenarioData d = new ScenarioData("B", "Insider Threat + Data Exfiltration");
        d.focusHosts.add("fs-01");

        // --- konfiguracija ---
        d.setup.add(new Host("fs-01", "fs-01.example.com", Tier.TIER_1, "db", "PCI"));
        d.setup.add(new HostBaseline("fs-01", 100.0, 40.0));     // 5× outbound = 200 MB
        d.setup.add(new SensitiveResource("/sensitive/pci_dump.csv"));
        d.setup.add(new UserActivityProfile("jdoe", 8, 50.0, 9, 18, "employees"));
        d.setup.add(new PeerGroupStats("employees", 30.0));
        d.setup.add(new UserRiskScore("jdoe", 78.0));

        // --- eventi ---
        // dnevna aktivnost jdoe: 28 logina (>3×8), 450MB (>5×50), u 21h
        //   R1.12 UnusualLoginFrequency, R1.13 UnusualDataVolume
        d.events.add(new UserActivityEvent("jdoe", "fs-01", 28, 450.0, 21, ts(t0, 0)));
        // pristup PCI dump-u u 21h (van radnog vremena) -> R1.11 SensitiveDataAccess, R2.10
        d.events.add(new ResourceAccessEvent("fs-01", "jdoe", "/sensitive/pci_dump.csv", 21, ts(t0, 900)));
        // outbound spike 320MB > 5× baseline -> R1.9 OutboundSpike, R2.7, R3.8 DataExfiltration
        d.events.add(new MetricEvent("fs-01", MetricType.OUTBOUND_TRAFFIC_MBPS, 320.0, ts(t0, 1200)));

        d.spanSeconds = 1200;
        return d;
    }

    private static Date ts(long t0, long offsetSeconds) {
        return new Date(t0 + offsetSeconds * 1000L);
    }

    // ---------------------------------------------------------------

    /** Podaci jednog scenarija: konfiguracija + vremenski poređani eventi. */
    public static final class ScenarioData {
        public final String id;
        public final String name;
        public final List<Object> setup = new ArrayList<>();
        public final List<Object> events = new ArrayList<>();
        public final List<String> focusHosts = new ArrayList<>();
        public long spanSeconds;

        public ScenarioData(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /** Kratak opis scenarija za listu u klijentu. */
    public static final class ScenarioInfo {
        public final String id;
        public final String name;
        public final String description;

        public ScenarioInfo(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }
}
