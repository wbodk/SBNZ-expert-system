package com.ftn.sbnz.service.scenario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ftn.sbnz.service.engine.IncidentEngineService;
import com.ftn.sbnz.service.scenario.TestDataFactory.ScenarioData;
import com.ftn.sbnz.service.scenario.TestDataFactory.ScenarioInfo;

/**
 * Reprodukuje demonstracioni scenario na deljenoj KieSession: reset -> ubaci
 * konfiguraciju i evente (sa timestamp-ovima) -> pomeri sat preko celog raspona
 * da bi vremenski prozori obuhvatili evente -> jedan fireAllRules (severity se
 * množi Tier-faktorom tačno jednom, na kraju).
 */
@Service
public class ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    private final IncidentEngineService engine;

    @Autowired
    public ScenarioService(IncidentEngineService engine) {
        this.engine = engine;
    }

    public List<ScenarioInfo> listScenarios() {
        return TestDataFactory.list();
    }

    public synchronized Map<String, Object> run(String id) {
        engine.reset();
        long t0 = engine.now();
        ScenarioData d = TestDataFactory.byId(id, t0);
        log.info("Pokrećem scenario {} ({}) — {} setup, {} eventi, raspon {}s",
                d.id, d.name, d.setup.size(), d.events.size(), d.spanSeconds);

        engine.insertAll(d.setup);
        // pomeri sat na kraj raspona da sliding-window upiti obuhvate evente
        engine.advanceClock(d.spanSeconds, TimeUnit.SECONDS);
        engine.insertAll(d.events);
        int fired = engine.fireAllRules();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("scenarioId", d.id);
        out.put("scenarioName", d.name);
        out.put("focusHosts", d.focusHosts);
        out.put("firedRules", fired);
        out.put("alerts", engine.getAlerts());
        out.put("severities", engine.getSeverities());
        out.put("responseActions", engine.getResponseActions());
        out.put("clock", engine.now());

        Map<String, Object> diagnosis = new LinkedHashMap<>();
        for (String host : d.focusHosts) {
            Map<String, Object> perHost = new LinkedHashMap<>();
            perHost.put("queries", engine.diagnose(host));
            perHost.put("whyNotRansomware", engine.whyNotTriggered("ransomwareRisk", host));
            diagnosis.put(host, perHost);
        }
        out.put("diagnosis", diagnosis);
        out.put("factsByType", engine.getAllFactsByType());
        return out;
    }
}
