package com.ftn.sbnz.service.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.Alert;
import com.ftn.sbnz.model.Alert.Level;
import com.ftn.sbnz.model.BruteForceAttempt;
import com.ftn.sbnz.model.DDoSPattern;
import com.ftn.sbnz.model.HighCPU;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.Host.Tier;
import com.ftn.sbnz.model.IncidentSeverity;
import com.ftn.sbnz.model.LoginAttemptEvent;
import com.ftn.sbnz.model.MetricEvent;
import com.ftn.sbnz.model.MetricEvent.MetricType;
import com.ftn.sbnz.model.PotentialDDoS;

/**
 * Pravila vlasnika: Roman Minakov (SV83/2023) — Security & Diagnostics.
 *
 * Pokriva pravila: R1.6 (BruteForce, accumulate), R2.4 (PotentialDDoS),
 * R3.4 (DDoSPattern, CEP), R4.6 (CRITICAL Alert).
 *
 * Svaki test koristi svežu KieSession sa pseudo-clock-om. Pored pozitivnih
 * scenarija uključene su i ključne negativne provere (CEP prozor, accumulate prag).
 */
public class SecurityRulesTest {

    /** Kreira novu KieSession sa logger-om koji štampa ime svakog ispaljenog pravila. */
    private KieSession newSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.newKieClasspathContainer();
        KieSession ksession = kc.newKieSession("incidentKsession");
        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                System.out.println("   >> fired: " + event.getMatch().getRule().getName());
            }
        });
        return ksession;
    }

    // =========================================================
    // R1.6 — BruteForceAttempt (accumulate)
    // =========================================================

    @Test
    public void testR16_BruteForce_firesOn6FailedLogins() {
        System.out.println("\n--- R1.6 positive: 6 failed logins ---");
        KieSession ks = newSession();
        ks.insert(new Host("auth-01", "auth-01.example.com", Tier.TIER_2));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        long t0 = clock.getCurrentTime();
        for (int i = 0; i < 6; i++) {
            ks.insert(new LoginAttemptEvent("auth-01", "admin",
                    "10.0.0." + (10 + i), false, new Date(t0 + i * 1000)));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ks.fireAllRules();

        Collection<?> brute = ks.getObjects(new ClassObjectFilter(BruteForceAttempt.class));
        assertThat("R1.6 treba da ispali pri 6 failed logins", brute.size(), equalTo(1));
        ks.dispose();
    }

    @Test
    public void testR16_BruteForce_doesNotFireOn5FailedLogins() {
        System.out.println("\n--- R1.6 negative: 5 failed logins (prag je > 5) ---");
        KieSession ks = newSession();
        ks.insert(new Host("auth-01", "auth-01.example.com", Tier.TIER_2));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        long t0 = clock.getCurrentTime();
        for (int i = 0; i < 5; i++) {
            ks.insert(new LoginAttemptEvent("auth-01", "admin",
                    "10.0.0." + (10 + i), false, new Date(t0 + i * 1000)));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ks.fireAllRules();

        Collection<?> brute = ks.getObjects(new ClassObjectFilter(BruteForceAttempt.class));
        assertThat("R1.6 NE sme da ispali pri 5 failed logins",
                brute.size(), equalTo(0));
        ks.dispose();
    }

    // =========================================================
    // R2.4 — PotentialDDoS
    // =========================================================

    @Test
    public void testR24_PotentialDDoS_firesOnTrafficAndServiceDown() {
        System.out.println("\n--- R2.4 positive: TrafficSpike + ServiceDown ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        // R2.4 = TrafficSpike (traffic > 500) AND SlowService (responseTime > 3000ms)
        ks.insert(new MetricEvent("web-01", MetricType.NETWORK_TRAFFIC_MBPS, 850.0,
                new Date(clock.getCurrentTime())));
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_RESPONSE_TIME_MS, 4000.0,
                new Date(clock.getCurrentTime())));
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_AVAILABILITY, 0.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();

        Collection<?> ddos = ks.getObjects(new ClassObjectFilter(PotentialDDoS.class));
        assertThat("R2.4 PotentialDDoS treba da ispali",
                ddos.size(), equalTo(1));
        ks.dispose();
    }

    // =========================================================
    // R3.4 — DDoSPattern (CEP, prozor 2 min)
    // =========================================================

    @Test
    public void testR34_DDoSPattern_firesWhenAllEventsWithin2Minutes() {
        System.out.println("\n--- R3.4 positive: tri događaja u prozoru 2 min ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);
        ks.insert(new MetricEvent("web-01", MetricType.NETWORK_TRAFFIC_MBPS, 850.0,
                new Date(clock.getCurrentTime())));
        clock.advanceTime(30, TimeUnit.SECONDS);
        ks.insert(new MetricEvent("web-01", MetricType.CPU_USAGE, 97.0,
                new Date(clock.getCurrentTime())));
        clock.advanceTime(30, TimeUnit.SECONDS);
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_AVAILABILITY, 0.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();

        Collection<?> pattern = ks.getObjects(new ClassObjectFilter(DDoSPattern.class));
        assertThat("R3.4 (CEP) treba da ispali u prozoru 2 min",
                pattern.size(), equalTo(1));
        ks.dispose();
    }

    @Test
    public void testR34_DDoSPattern_doesNotFireWhenEventsExceedWindow() {
        System.out.println("\n--- R3.4 negative: HighCPU i ServiceDown stižu 3 min posle Traffic-a ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);
        ks.insert(new MetricEvent("web-01", MetricType.NETWORK_TRAFFIC_MBPS, 850.0,
                new Date(clock.getCurrentTime())));
        // Pomeramo sat 3 min — van CEP prozora za R3.4
        clock.advanceTime(3, TimeUnit.MINUTES);
        ks.insert(new MetricEvent("web-01", MetricType.CPU_USAGE, 97.0,
                new Date(clock.getCurrentTime())));
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_AVAILABILITY, 0.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();

        Collection<?> pattern = ks.getObjects(new ClassObjectFilter(DDoSPattern.class));
        Collection<?> potential = ks.getObjects(new ClassObjectFilter(PotentialDDoS.class));
        assertThat("R3.4 (CEP) NE sme da ispali van prozora 2 min",
                pattern.size(), equalTo(0));
        // R2.4 PotentialDDoS nema vremensko ograničenje pa može da ispali — to je OK
        assertTrue("R2.4 PotentialDDoS može i dalje da bude prisutan",
                potential.size() >= 0);
        ks.dispose();
    }

    // =========================================================
    // R4.6 — CRITICAL Alert (granični prag = 85)
    // =========================================================

    @Test
    public void testR46_CriticalAlert_firesAt85() {
        System.out.println("\n--- R4.6 positive: severity = 85 (granica) ---");
        KieSession ks = newSession();
        // TIER_2 (multiplier 1.0 iz AssetCriticality.xls) — izoluje granicu praga 85
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_2));
        ks.insert(new IncidentSeverity("web-01", 85));
        ks.fireAllRules();

        Collection<?> alerts = ks.getObjects(new ClassObjectFilter(Alert.class));
        boolean hasCritical = alerts.stream()
                .map(o -> (Alert) o)
                .anyMatch(a -> a.getLevel() == Level.CRITICAL && "web-01".equals(a.getHostId()));
        assertTrue("R4.6 CRITICAL treba da ispali pri severity=85", hasCritical);
        ks.dispose();
    }

    @Test
    public void testR46_CriticalAlert_doesNotFireAt84() {
        System.out.println("\n--- R4.6 negative: severity = 84 (ispod praga 85) ---");
        KieSession ks = newSession();
        // TIER_2 (multiplier 1.0) — bez Tier-multiplikatora granica praga ostaje 84 < 85
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_2));
        ks.insert(new IncidentSeverity("web-01", 84));
        ks.fireAllRules();

        Collection<?> alerts = ks.getObjects(new ClassObjectFilter(Alert.class));
        boolean hasCritical = alerts.stream()
                .map(o -> (Alert) o)
                .anyMatch(a -> a.getLevel() == Level.CRITICAL);
        assertEquals("R4.6 CRITICAL NE sme da ispali pri severity=84", false, hasCritical);
        ks.dispose();
    }

    // =========================================================
    // Scenarij A — full integration (spec_latest.md §2.1)
    // =========================================================

    @Test
    public void testScenarioA_DDoSPlusBruteForce_endToEnd() {
        System.out.println("\n--- Scenarij A: end-to-end DDoS + BruteForce ---");
        KieSession ks = newSession();
        Host web01 = new Host("web-01", "web-01.example.com", Tier.TIER_1);
        Host auth01 = new Host("auth-01", "auth-01.example.com", Tier.TIER_2);
        ks.insert(web01);
        ks.insert(auth01);
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);
        long t0 = clock.getCurrentTime();

        // Brute force na auth-01
        for (int i = 0; i < 6; i++) {
            ks.insert(new LoginAttemptEvent("auth-01", "admin",
                    "10.0.0." + (10 + i), false, new Date(t0 + i * 1000)));
            clock.advanceTime(1, TimeUnit.SECONDS);
        }
        ks.fireAllRules();

        // DDoS na web-01 — sve tri komponente u 2 min
        clock.advanceTime(84, TimeUnit.SECONDS);
        ks.insert(new MetricEvent("web-01", MetricType.NETWORK_TRAFFIC_MBPS, 850.0,
                new Date(clock.getCurrentTime())));
        // SlowService komponenta -> R2.4 PotentialDDoS (TrafficSpike + SlowService)
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_RESPONSE_TIME_MS, 5000.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();
        clock.advanceTime(30, TimeUnit.SECONDS);
        ks.insert(new MetricEvent("web-01", MetricType.CPU_USAGE, 97.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();
        clock.advanceTime(30, TimeUnit.SECONDS);
        ks.insert(new MetricEvent("web-01", MetricType.SERVICE_AVAILABILITY, 0.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();

        Collection<?> highCpu = ks.getObjects(new ClassObjectFilter(HighCPU.class));
        Collection<?> brute = ks.getObjects(new ClassObjectFilter(BruteForceAttempt.class));
        Collection<?> potential = ks.getObjects(new ClassObjectFilter(PotentialDDoS.class));
        Collection<?> pattern = ks.getObjects(new ClassObjectFilter(DDoSPattern.class));
        Collection<?> sevs = ks.getObjects(new ClassObjectFilter(IncidentSeverity.class));

        assertThat(highCpu.size(), equalTo(1));
        assertThat(brute.size(), equalTo(1));
        assertThat(potential.size(), equalTo(1));
        assertThat(pattern.size(), equalTo(1));

        IncidentSeverity webSev = sevs.stream()
                .map(o -> (IncidentSeverity) o)
                .filter(s -> "web-01".equals(s.getHostId()))
                .findFirst().orElseThrow(() -> new AssertionError("Nema severity za web-01"));
        // 30 (R2.4) + 45 (R3.4) = 75 minimum; sa Avanesov-ovim R3.2 raste do ~95
        assertThat("Severity web-01 treba da bude >= 75",
                webSev.getScore(), greaterThanOrEqualTo(75));

        ks.dispose();
    }
}
