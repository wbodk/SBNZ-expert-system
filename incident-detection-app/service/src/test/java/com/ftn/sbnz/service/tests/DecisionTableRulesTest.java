package com.ftn.sbnz.service.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.ftn.sbnz.model.Alert;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.Host.Tier;
import com.ftn.sbnz.model.IncidentSeverity;
import com.ftn.sbnz.model.MetricEvent;
import com.ftn.sbnz.model.MetricEvent.MetricType;
import com.ftn.sbnz.model.PerfThresholdBreach;
import com.ftn.sbnz.model.ResponseAction;

/**
 * XLS decision tables (Templates — Avanesov): AssetCriticality, PerServiceThresholds,
 * CompliancePolicy. Tabele se auto-kompajliraju iz .xls u kbase.
 */
public class DecisionTableRulesTest {

    private KieSession newSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.newKieClasspathContainer();
        return kc.newKieSession("incidentKsession");
    }

    @Test
    public void testAssetCriticality_tier1MultipliesSeverity() {
        System.out.println("\n--- XLS AssetCriticality: TIER_1 ×1.5 ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1, "web", null));
        ks.insert(new IncidentSeverity("web-01", 40));
        ks.fireAllRules();

        IncidentSeverity sev = ks.getObjects(new ClassObjectFilter(IncidentSeverity.class))
                .stream().map(o -> (IncidentSeverity) o)
                .filter(s -> "web-01".equals(s.getHostId())).findFirst().orElseThrow(AssertionError::new);
        // 40 × 1.5 = 60
        assertThat("TIER_1 multiplikator 1.5: 40 -> 60", sev.getScore(), equalTo(60));
        ks.dispose();
    }

    @Test
    public void testAssetCriticality_tier3ReducesSeverity() {
        System.out.println("\n--- XLS AssetCriticality: TIER_3 ×0.7 ---");
        KieSession ks = newSession();
        ks.insert(new Host("batch-01", "batch-01.example.com", Tier.TIER_3, "batch", null));
        ks.insert(new IncidentSeverity("batch-01", 100));
        ks.fireAllRules();

        IncidentSeverity sev = ks.getObjects(new ClassObjectFilter(IncidentSeverity.class))
                .stream().map(o -> (IncidentSeverity) o)
                .filter(s -> "batch-01".equals(s.getHostId())).findFirst().orElseThrow(AssertionError::new);
        // 100 × 0.7 = 70
        assertThat("TIER_3 multiplikator 0.7: 100 -> 70", sev.getScore(), equalTo(70));
        ks.dispose();
    }

    @Test
    public void testPerServiceThresholds_dbClassLowerCpuThreshold() {
        System.out.println("\n--- XLS PerServiceThresholds: db prag CPU 80 ---");
        KieSession ks = newSession();
        // db klasa: prag 80; CPU 82 > 80 -> PerfThresholdBreach
        ks.insert(new Host("db-01", "db-01.example.com", Tier.TIER_2, "db", null));
        ks.fireAllRules();
        ks.insert(new MetricEvent("db-01", MetricType.CPU_USAGE, 82.0, new Date()));
        ks.fireAllRules();

        Collection<?> breaches = ks.getObjects(new ClassObjectFilter(PerfThresholdBreach.class));
        assertThat("db klasa: CPU 82 > prag 80 -> breach", breaches.size(), equalTo(1));
        ks.dispose();
    }

    @Test
    public void testCompliancePolicy_pciNotificationAfterAlert() {
        System.out.println("\n--- XLS CompliancePolicy: PCI -> notifyCompliance ---");
        KieSession ks = newSession();
        ks.insert(new Host("fs-01", "fs-01.example.com", Tier.TIER_2, "db", "PCI"));
        ks.insert(new IncidentSeverity("fs-01", 40));   // -> MEDIUM alert -> compliance reaguje
        ks.fireAllRules();

        Collection<ResponseAction> actions = (Collection<ResponseAction>) (Collection<?>)
                ks.getObjects(new ClassObjectFilter(ResponseAction.class));
        boolean pciNotified = actions.stream()
                .anyMatch(a -> "fs-01".equals(a.getHostId()) && a.getAction().contains("notifyCompliance_PCI"));
        assertTrue("PCI host sa alertom treba da pokrene notifyCompliance_PCI", pciNotified);
        // a postoji i Alert
        assertTrue("Treba bar jedan Alert", ks.getObjects(new ClassObjectFilter(Alert.class)).size() >= 1);
        ks.dispose();
    }
}
