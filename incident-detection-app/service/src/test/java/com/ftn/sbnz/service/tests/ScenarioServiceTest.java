package com.ftn.sbnz.service.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;

import com.ftn.sbnz.model.Alert;
import com.ftn.sbnz.model.Alert.Level;
import com.ftn.sbnz.model.ResponseAction;
import com.ftn.sbnz.service.engine.IncidentEngineService;
import com.ftn.sbnz.service.scenario.ScenarioService;

/**
 * Integracioni test scenario runner-a (Faza 5) — reprodukuje spec §2.1 i §2.2
 * preko ScenarioService + IncidentEngineService nad classpath KieContainer-om.
 */
public class ScenarioServiceTest {

    private IncidentEngineService engine;
    private ScenarioService scenarios;

    @Before
    public void setUp() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.newKieClasspathContainer();
        engine = new IncidentEngineService(kc);
        engine.init();
        scenarios = new ScenarioService(engine);
    }

    private boolean hasAlert(String host, Level level) {
        for (Alert a : engine.getAlerts()) {
            if (host.equals(a.getHostId()) && a.getLevel() == level) {
                return true;
            }
        }
        return false;
    }

    private boolean hasResponse(String host, String actionFragment) {
        for (ResponseAction r : engine.getResponseActions()) {
            if (host.equals(r.getHostId()) && r.getAction().contains(actionFragment)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testScenarioA_ddosAndCompromise_critical() {
        System.out.println("\n=== Scenario A (DDoS + Compromised) ===");
        scenarios.run("A");

        assertTrue("web-01 treba da bude CRITICAL", hasAlert("web-01", Level.CRITICAL));

        Map<String, Boolean> diag = engine.diagnose("web-01");
        assertTrue("isSystemAtRisk(web-01)", diag.get("isSystemAtRisk"));
        assertTrue("hasSecurityThreat(web-01)", diag.get("hasSecurityThreat"));
        assertTrue("hasAvailabilityIssue(web-01)", diag.get("hasAvailabilityIssue"));
        assertTrue("hasPerformanceDegradation(web-01)", diag.get("hasPerformanceDegradation"));

        // kompromitovan nalog -> lockAccount; CRITICAL -> isolateHost
        assertTrue("R4.7 lockAccount response", hasResponse("web-01", "lockAccount"));
        assertTrue("R4.6 isolateHost response", hasResponse("web-01", "isolateHost"));
    }

    @Test
    public void testScenarioB_insiderAndExfiltration_critical() {
        System.out.println("\n=== Scenario B (Insider + Exfiltration) ===");
        scenarios.run("B");

        assertTrue("fs-01 treba da bude CRITICAL", hasAlert("fs-01", Level.CRITICAL));

        Map<String, Boolean> diag = engine.diagnose("fs-01");
        assertTrue("isSystemAtRisk(fs-01)", diag.get("isSystemAtRisk"));
        assertTrue("hasDataBreachRisk(fs-01)", diag.get("hasDataBreachRisk"));
        assertTrue("hasInsiderThreat(fs-01)", diag.get("hasInsiderThreat"));

        // R4.10 InsiderThreat -> revokeAccess; R4.8 DataExfiltration -> blockOutboundTraffic;
        // R4.14 PCI -> notifyCompliance
        assertTrue("R4.10 revokeAccess response", hasResponse("fs-01", "revokeAccess"));
        assertTrue("R4.8 blockOutboundTraffic response", hasResponse("fs-01", "blockOutboundTraffic"));
        assertTrue("R4.14 notifyCompliance_PCI response", hasResponse("fs-01", "notifyCompliance_PCI"));

        // Why-not: ransomware nije aktiviran (nema MassFileModification)
        boolean massFileMissing = engine.whyNotTriggered("ransomwareRisk", "fs-01").stream()
                .anyMatch(f -> "MassFileModification".equals(f.getId()));
        assertTrue("why-not ransomware -> MassFileModification nedostaje", massFileMissing);
    }
}
