package com.ftn.sbnz.service.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.drools.core.time.SessionPseudoClock;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.model.Alert;
import com.ftn.sbnz.model.IncidentFactor;
import com.ftn.sbnz.model.IncidentSeverity;
import com.ftn.sbnz.model.ResponseAction;

/**
 * Drži jednu deljenu (persistent) KieSession sa pseudo-clock-om i izlaže
 * operacije nad njom: ubacivanje činjenica, fire, upite (alerts/severity),
 * backward-chaining dijagnozu (Q1–Q8), kontrolu sata i reset.
 *
 * KieSession nije thread-safe — sve javne metode su sinhronizovane.
 */
@Service
public class IncidentEngineService {

    private static final Logger log = LoggerFactory.getLogger(IncidentEngineService.class);

    private final KieContainer kieContainer;
    private KieSession session;

    @Autowired
    public IncidentEngineService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @PostConstruct
    public synchronized void init() {
        this.session = kieContainer.newKieSession("incidentKsession");
        log.info("IncidentEngineService: deljena KieSession kreirana (pseudo-clock, stream mode)");
    }

    /** Rekreira sesiju — briše akumulirano stanje između demoa. */
    public synchronized void reset() {
        if (session != null) {
            session.dispose();
        }
        this.session = kieContainer.newKieSession("incidentKsession");
        log.info("IncidentEngineService: sesija resetovana");
    }

    // ---------------------------------------------------------------
    // Ubacivanje činjenica i pokretanje pravila
    // ---------------------------------------------------------------

    /** Ubacuje proizvoljnu činjenicu (model POJO). */
    public synchronized void insert(Object fact) {
        session.insert(fact);
    }

    public synchronized void insertAll(List<?> facts) {
        for (Object f : facts) {
            session.insert(f);
        }
    }

    public synchronized int fireAllRules() {
        int n = session.fireAllRules();
        log.info("fireAllRules -> {} aktivacija", n);
        return n;
    }

    /** Trenutno vreme pseudo-sata (ms). Eventi bez eksplicitnog timestamp-a koriste ga. */
    public synchronized long now() {
        return ((SessionPseudoClock) session.getSessionClock()).getCurrentTime();
    }

    public synchronized Date nowDate() {
        return new Date(now());
    }

    /** Pomera pseudo-sat (npr. za simulaciju vremenskih prozora CEP/accumulate). */
    public synchronized long advanceClock(long amount, TimeUnit unit) {
        SessionPseudoClock clock = session.getSessionClock();
        clock.advanceTime(amount, unit);
        return clock.getCurrentTime();
    }

    // ---------------------------------------------------------------
    // Upiti nad radnom memorijom
    // ---------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public synchronized <T> List<T> getFacts(Class<T> type) {
        List<T> out = new ArrayList<>();
        for (Object o : session.getObjects(new ClassObjectFilter(type))) {
            out.add((T) o);
        }
        return out;
    }

    public List<Alert> getAlerts() {
        return getFacts(Alert.class);
    }

    public List<IncidentSeverity> getSeverities() {
        return getFacts(IncidentSeverity.class);
    }

    public List<ResponseAction> getResponseActions() {
        return getFacts(ResponseAction.class);
    }

    /** Sve činjenice u radnoj memoriji, grupisane po jednostavnom imenu klase. */
    public synchronized Map<String, List<Object>> getAllFactsByType() {
        Map<String, List<Object>> grouped = new LinkedHashMap<>();
        for (Object o : session.getObjects()) {
            grouped.computeIfAbsent(o.getClass().getSimpleName(), k -> new ArrayList<>()).add(o);
        }
        return grouped;
    }

    // ---------------------------------------------------------------
    // Backward chaining — dijagnoza (Q1–Q8)
    // ---------------------------------------------------------------

    private synchronized boolean queryTrue(String query, String host) {
        QueryResults r = session.getQueryResults(query, host);
        return r.size() > 0;
    }

    /** Q1–Q7 kao mapa naziv->boolean za dati host. */
    public synchronized Map<String, Boolean> diagnose(String host) {
        Map<String, Boolean> d = new LinkedHashMap<>();
        d.put("isSystemAtRisk", queryTrue("isSystemAtRisk", host));
        d.put("hasPerformanceDegradation", queryTrue("hasPerformanceDegradation", host));
        d.put("hasSecurityThreat", queryTrue("hasSecurityThreat", host));
        d.put("hasAvailabilityIssue", queryTrue("hasAvailabilityIssue", host));
        d.put("hasDataBreachRisk", queryTrue("hasDataBreachRisk", host));
        d.put("hasInsiderThreat", queryTrue("hasInsiderThreat", host));
        d.put("hasRansomwareRisk", queryTrue("hasRansomwareRisk", host));
        return d;
    }

    /** Q8 — vraća id-jeve nezadovoljenih uslova koji su sprečili dati incident. */
    public synchronized List<IncidentFactor> whyNotTriggered(String incidentType, String host) {
        List<IncidentFactor> out = new ArrayList<>();
        QueryResults r = session.getQueryResults("whyNotTriggered", incidentType, host);
        for (QueryResultsRow row : r) {
            out.add((IncidentFactor) row.get("$factor"));
        }
        return out;
    }
}
