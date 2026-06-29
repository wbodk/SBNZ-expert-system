package com.ftn.sbnz.service.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import com.ftn.sbnz.model.DDoSPattern;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.Host.Tier;
import com.ftn.sbnz.model.IncidentFactor;

/**
 * Backward chaining — rekurzivni query-ji nad stablom zavisnosti (Minakov).
 * Pokriva Q1 (isSystemAtRisk), Q3 (hasSecurityThreat) i Q8 (whyNotTriggered).
 */
public class BackwardChainingTest {

    private KieSession newSession() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.newKieClasspathContainer();
        return kc.newKieSession("incidentKsession");
    }

    private int querySize(KieSession ks, String query, Object... args) {
        QueryResults r = ks.getQueryResults(query, args);
        return r.size();
    }

    @Test
    public void testIsSystemAtRisk_trueWhenDDoSPatternPresent() {
        System.out.println("\n--- BC Q1: isSystemAtRisk(web-01) sa DDoSPattern ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1));
        ks.insert(new DDoSPattern("web-01"));   // list: ddos -> securityThreat -> systemAtRisk
        ks.fireAllRules();

        assertTrue("Q1 isSystemAtRisk treba da bude tačno (DDoSPattern -> securityThreat -> systemAtRisk)",
                querySize(ks, "isSystemAtRisk", "web-01") > 0);
        assertTrue("Q3 hasSecurityThreat treba da bude tačno",
                querySize(ks, "hasSecurityThreat", "web-01") > 0);
        // Nije bilo performansnih ni ransomware indikatora
        assertEquals("Q2 hasPerformanceDegradation treba da bude netačno",
                0, querySize(ks, "hasPerformanceDegradation", "web-01"));
        assertEquals("Q7 hasRansomwareRisk treba da bude netačno",
                0, querySize(ks, "hasRansomwareRisk", "web-01"));
        ks.dispose();
    }

    @Test
    public void testIsSystemAtRisk_falseForCleanHost() {
        System.out.println("\n--- BC Q1 negativno: čist host ---");
        KieSession ks = newSession();
        ks.insert(new Host("db-09", "db-09.example.com", Tier.TIER_3));
        ks.fireAllRules();

        assertEquals("Čist host ne sme biti at-risk",
                0, querySize(ks, "isSystemAtRisk", "db-09"));
        ks.dispose();
    }

    @Test
    public void testWhyNotTriggered_ransomwareReturnsMissingFactors() {
        System.out.println("\n--- BC Q8: whyNotTriggered(ransomwareRisk, web-01) ---");
        KieSession ks = newSession();
        ks.insert(new Host("web-01", "web-01.example.com", Tier.TIER_1));
        ks.insert(new DDoSPattern("web-01"));   // DDoS jeste, ransomware nije
        ks.fireAllRules();

        QueryResults r = ks.getQueryResults("whyNotTriggered", "ransomwareRisk", "web-01");
        boolean massFileMissing = false;
        for (QueryResultsRow row : r) {
            IncidentFactor f = (IncidentFactor) row.get("$factor");
            System.out.println("   nedostaje: " + f.getId() + " (satisfied=" + f.isSatisfied() + ")");
            assertFalse("Why-not vraća samo nezadovoljene faktore", f.isSatisfied());
            if ("MassFileModification".equals(f.getId())) {
                massFileMissing = true;
            }
        }
        assertTrue("Why-not za ransomware treba da uključi MassFileModification kao nedostajući uslov",
                massFileMissing);
        ks.dispose();
    }
}
