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
import com.ftn.sbnz.model.HighCPU;
import com.ftn.sbnz.model.HighMemory;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.Host.Tier;
import com.ftn.sbnz.model.IncidentSeverity;
import com.ftn.sbnz.model.MetricEvent;
import com.ftn.sbnz.model.MetricEvent.MetricType;
import com.ftn.sbnz.model.ResourceExhaustion;
import com.ftn.sbnz.model.SustainedHighLoad;

/**
 * Pravila vlasnika: Avanesov Roman (SV88/2024) — Infrastructure & UBA.
 *
 * Pokriva pravila: R1.1 (HighCPU), R1.2 (HighMemory), R2.1 (ResourceExhaustion),
 * R3.2 (SustainedHighLoad, accumulate AVG nad sliding window-om), R4.4 (MEDIUM Alert).
 *
 * Svaki test koristi svežu KieSession sa pseudo-clock-om. Pored pozitivnih
 * scenarija uključene su negativne provere granica i ključna negativna
 * provera za accumulate (prosečna vrednost ispod praga).
 */
public class InfrastructureRulesTest {

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
    // R1.1 — HighCPU (cpuUsage > 90)
    // =========================================================

    @Test
    public void testR11_HighCPU_firesAbove90() {
        System.out.println("\n--- R1.1 positive: cpu = 91 ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, 91.0, new Date()));
        ks.fireAllRules();
        assertThat(ks.getObjects(new ClassObjectFilter(HighCPU.class)).size(),
                equalTo(1));
        ks.dispose();
    }

    @Test
    public void testR11_HighCPU_doesNotFireAt90() {
        System.out.println("\n--- R1.1 negative: cpu = 90 (granica, strogo veće) ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, 90.0, new Date()));
        ks.fireAllRules();
        assertThat(ks.getObjects(new ClassObjectFilter(HighCPU.class)).size(),
                equalTo(0));
        ks.dispose();
    }

    // =========================================================
    // R1.2 — HighMemory (memUsage > 95)
    // =========================================================

    @Test
    public void testR12_HighMemory_firesAbove95() {
        System.out.println("\n--- R1.2 positive: mem = 96 ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.MEMORY_USAGE, 96.0, new Date()));
        ks.fireAllRules();
        assertThat(ks.getObjects(new ClassObjectFilter(HighMemory.class)).size(),
                equalTo(1));
        ks.dispose();
    }

    // =========================================================
    // R2.1 — ResourceExhaustion (HighCPU AND HighMemory)
    // =========================================================

    @Test
    public void testR21_ResourceExhaustion_requiresBothFacts() {
        System.out.println("\n--- R2.1 positive: HighCPU + HighMemory za isti host ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, 95.0, new Date()));
        ks.insert(new MetricEvent("db-01", MetricType.MEMORY_USAGE, 97.0, new Date()));
        ks.fireAllRules();
        assertThat(ks.getObjects(new ClassObjectFilter(ResourceExhaustion.class)).size(),
                equalTo(1));
        ks.dispose();
    }

    @Test
    public void testR21_ResourceExhaustion_doesNotFireWithoutHighMemory() {
        System.out.println("\n--- R2.1 negative: samo HighCPU (bez HighMemory) ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, 95.0, new Date()));
        ks.fireAllRules();
        assertThat(ks.getObjects(new ClassObjectFilter(ResourceExhaustion.class)).size(),
                equalTo(0));
        ks.dispose();
    }

    // =========================================================
    // R3.2 — SustainedHighLoad (accumulate AVG > 85 over 30min window)
    // =========================================================

    @Test
    public void testR32_SustainedHighLoad_firesWhenAvgAbove85() {
        System.out.println("\n--- R3.2 positive: niz CPU očitavanja sa AVG > 85 ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);
        double[] cpuReadings = {88, 91, 87, 93, 95, 92, 89, 90, 94, 91};
        for (double cpu : cpuReadings) {
            ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, cpu,
                    new Date(clock.getCurrentTime())));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }
        ks.fireAllRules();

        assertThat("R3.2 treba da ispali kad je AVG CPU > 85",
                ks.getObjects(new ClassObjectFilter(SustainedHighLoad.class)).size(),
                equalTo(1));
        ks.dispose();
    }

    @Test
    public void testR32_SustainedHighLoad_doesNotFireWhenAvgBelow85() {
        System.out.println("\n--- R3.2 negative: niz CPU očitavanja sa AVG <= 85 ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);
        // AVG = 75 — ispod praga 85
        double[] cpuReadings = {70, 75, 78, 72, 80, 76, 74, 72, 78, 75};
        for (double cpu : cpuReadings) {
            ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, cpu,
                    new Date(clock.getCurrentTime())));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }
        ks.fireAllRules();

        assertThat("R3.2 NE sme da ispali kad je AVG CPU <= 85",
                ks.getObjects(new ClassObjectFilter(SustainedHighLoad.class)).size(),
                equalTo(0));
        ks.dispose();
    }

    // =========================================================
    // R4.4 — MEDIUM Alert (severity ∈ [30, 60))
    // =========================================================

    @Test
    public void testR44_MediumAlert_firesAt40() {
        System.out.println("\n--- R4.4 positive: severity = 40 ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.insert(new IncidentSeverity("db-01", 40));
        ks.fireAllRules();

        Collection<?> alerts = ks.getObjects(new ClassObjectFilter(Alert.class));
        boolean hasMedium = alerts.stream()
                .map(o -> (Alert) o)
                .anyMatch(a -> a.getLevel() == Level.MEDIUM && "db-01".equals(a.getHostId()));
        assertTrue("R4.4 MEDIUM treba da ispali pri severity=40", hasMedium);
        ks.dispose();
    }

    @Test
    public void testR43_LowAlert_firesBelow30() {
        // Spec R4.3: severity ∈ (0, 30) -> Alert LOW (a ne "bez alerta").
        System.out.println("\n--- R4.3 positive: severity = 29 -> LOW ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.insert(new IncidentSeverity("db-01", 29));
        ks.fireAllRules();

        Collection<?> alerts = ks.getObjects(new ClassObjectFilter(Alert.class));
        boolean hasLow = alerts.stream()
                .map(o -> (Alert) o)
                .anyMatch(a -> a.getLevel() == Level.LOW && "db-01".equals(a.getHostId()));
        assertTrue("R4.3 LOW treba da ispali pri severity=29", hasLow);
        ks.dispose();
    }

    // =========================================================
    // Full integration scenarij — Avanesov-ova polovina
    // =========================================================

    @Test
    public void testInfraScenario_endToEnd() {
        System.out.println("\n--- Infra scenarij: ResourceExhaustion + SustainedHighLoad → MEDIUM ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2));
        ks.fireAllRules();

        SessionPseudoClock clock = ks.getSessionClock();
        clock.advanceTime(10, TimeUnit.MINUTES);

        // Faza 1: kontinuirano CPU > 85 da pokrene R3.2 SustainedHighLoad (+20)
        double[] cpuReadings = {88, 91, 87, 93, 95, 92, 89, 90, 94, 91, 96, 88, 92};
        for (double cpu : cpuReadings) {
            ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, cpu,
                    new Date(clock.getCurrentTime())));
            clock.advanceTime(2, TimeUnit.MINUTES);
        }

        // Faza 2: memory spike → R1.2 HighMemory + R2.1 ResourceExhaustion (+20)
        ks.insert(new MetricEvent("db-01", MetricType.MEMORY_USAGE, 97.0,
                new Date(clock.getCurrentTime())));
        ks.fireAllRules();

        assertThat(ks.getObjects(new ClassObjectFilter(HighCPU.class)).size(),
                equalTo(1));
        assertThat(ks.getObjects(new ClassObjectFilter(HighMemory.class)).size(),
                equalTo(1));
        assertThat(ks.getObjects(new ClassObjectFilter(ResourceExhaustion.class)).size(),
                equalTo(1));
        assertThat(ks.getObjects(new ClassObjectFilter(SustainedHighLoad.class)).size(),
                equalTo(1));

        IncidentSeverity sev = ks.getObjects(new ClassObjectFilter(IncidentSeverity.class))
                .stream().map(o -> (IncidentSeverity) o)
                .filter(s -> "db-01".equals(s.getHostId()))
                .findFirst().orElseThrow(() -> new AssertionError("Nema severity za db-01"));
        assertThat("Severity = 20 + 20 = 40",
                sev.getScore(), greaterThanOrEqualTo(40));

        Collection<?> alerts = ks.getObjects(new ClassObjectFilter(Alert.class));
        assertTrue("Treba bar jedan Alert", alerts.size() >= 1);
        Alert a = (Alert) alerts.iterator().next();
        assertEquals("Level treba da bude MEDIUM", Level.MEDIUM, a.getLevel());

        ks.dispose();
    }
}
