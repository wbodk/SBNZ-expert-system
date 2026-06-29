package com.ftn.sbnz.service.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ftn.sbnz.model.BlacklistedIp;
import com.ftn.sbnz.model.FileEvent;
import com.ftn.sbnz.model.Host;
import com.ftn.sbnz.model.HostBaseline;
import com.ftn.sbnz.model.IncidentFactor;
import com.ftn.sbnz.model.LoginAttemptEvent;
import com.ftn.sbnz.model.MetricEvent;
import com.ftn.sbnz.model.NetworkConnectionEvent;
import com.ftn.sbnz.model.PeerGroupStats;
import com.ftn.sbnz.model.ResourceAccessEvent;
import com.ftn.sbnz.model.ScheduledMaintenance;
import com.ftn.sbnz.model.SensitiveResource;
import com.ftn.sbnz.model.UserActivityEvent;
import com.ftn.sbnz.model.UserActivityProfile;
import com.ftn.sbnz.model.UserRiskScore;
import com.ftn.sbnz.service.dto.Requests.ClockAdvanceRequest;
import com.ftn.sbnz.service.dto.Requests.FileEventRequest;
import com.ftn.sbnz.service.dto.Requests.LoginEventRequest;
import com.ftn.sbnz.service.dto.Requests.MetricEventRequest;
import com.ftn.sbnz.service.dto.Requests.NetworkEventRequest;
import com.ftn.sbnz.service.dto.Requests.ResourceAccessRequest;
import com.ftn.sbnz.service.dto.Requests.UserActivityRequest;
import com.ftn.sbnz.service.engine.IncidentEngineService;
import com.ftn.sbnz.service.scenario.ScenarioService;
import com.ftn.sbnz.service.scenario.TestDataFactory.ScenarioInfo;

/**
 * REST API nad deljenom KieSession. Konfiguracione činjenice se primaju kao
 * model POJO-i; eventi kao DTO-i (timestamp dodeljuje engine sa pseudo-sata).
 * Insert NE pokreće pravila automatski — pozvati POST /api/fire (ili /scenarios/{id}/run).
 */
@RestController
@RequestMapping("/api")
public class IncidentController {

    private final IncidentEngineService engine;
    private final ScenarioService scenarios;

    @Autowired
    public IncidentController(IncidentEngineService engine, ScenarioService scenarios) {
        this.engine = engine;
        this.scenarios = scenarios;
    }

    // ---------------- Scenariji (gotove demonstracije) ----------------

    @GetMapping("/scenarios")
    public List<ScenarioInfo> scenarios() {
        return scenarios.listScenarios();
    }

    @PostMapping("/scenarios/{id}/run")
    public Map<String, Object> runScenario(@PathVariable String id) {
        return scenarios.run(id);
    }

    // ---------------- Konfiguracija ----------------

    @PostMapping("/hosts")
    public Map<String, Object> addHost(@RequestBody Host host) {
        engine.insert(host);
        return state();
    }

    @PostMapping("/config/baseline")
    public Map<String, Object> addBaseline(@RequestBody HostBaseline b) {
        engine.insert(b);
        return state();
    }

    @PostMapping("/config/blacklist")
    public Map<String, Object> addBlacklist(@RequestBody BlacklistedIp ip) {
        engine.insert(ip);
        return state();
    }

    @PostMapping("/config/sensitive-resource")
    public Map<String, Object> addSensitive(@RequestBody SensitiveResource r) {
        engine.insert(r);
        return state();
    }

    @PostMapping("/config/user-profile")
    public Map<String, Object> addUserProfile(@RequestBody UserActivityProfile p) {
        engine.insert(p);
        return state();
    }

    @PostMapping("/config/peer-group")
    public Map<String, Object> addPeerGroup(@RequestBody PeerGroupStats p) {
        engine.insert(p);
        return state();
    }

    @PostMapping("/config/user-risk")
    public Map<String, Object> addUserRisk(@RequestBody UserRiskScore r) {
        engine.insert(r);
        return state();
    }

    @PostMapping("/config/maintenance")
    public Map<String, Object> addMaintenance(@RequestBody ScheduledMaintenance m) {
        engine.insert(m);
        return state();
    }

    // ---------------- Eventi ----------------

    @PostMapping("/events/metric")
    public Map<String, Object> addMetric(@RequestBody MetricEventRequest r) {
        engine.insert(new MetricEvent(r.hostId, r.metricType, r.value, engine.nowDate()));
        return state();
    }

    @PostMapping("/events/login")
    public Map<String, Object> addLogin(@RequestBody LoginEventRequest r) {
        engine.insert(new LoginAttemptEvent(r.hostId, r.userId, r.sourceIp, r.success, r.loginHour, engine.nowDate()));
        return state();
    }

    @PostMapping("/events/resource-access")
    public Map<String, Object> addResourceAccess(@RequestBody ResourceAccessRequest r) {
        engine.insert(new ResourceAccessEvent(r.hostId, r.userId, r.resourceId, r.accessHour, engine.nowDate()));
        return state();
    }

    @PostMapping("/events/user-activity")
    public Map<String, Object> addUserActivity(@RequestBody UserActivityRequest r) {
        engine.insert(new UserActivityEvent(r.userId, r.hostId, r.dailyLoginCount,
                r.dataDownloadVolumeMb, r.activityHour, engine.nowDate()));
        return state();
    }

    @PostMapping("/events/file")
    public Map<String, Object> addFile(@RequestBody FileEventRequest r) {
        engine.insert(new FileEvent(r.hostId, r.fileCount, r.extension, engine.nowDate()));
        return state();
    }

    @PostMapping("/events/network")
    public Map<String, Object> addNetwork(@RequestBody NetworkEventRequest r) {
        engine.insert(new NetworkConnectionEvent(r.hostId, r.destIp, r.bytesOutMb, engine.nowDate()));
        return state();
    }

    // ---------------- Kontrola sesije ----------------

    @PostMapping("/fire")
    public Map<String, Object> fire() {
        int fired = engine.fireAllRules();
        Map<String, Object> s = state();
        s.put("firedRules", fired);
        return s;
    }

    @PostMapping("/clock/advance")
    public Map<String, Object> advanceClock(@RequestBody ClockAdvanceRequest r) {
        long t = engine.advanceClock(r.amount, TimeUnit.valueOf(r.unit.toUpperCase()));
        Map<String, Object> out = state();
        out.put("clock", t);
        return out;
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        engine.reset();
        return state();
    }

    // ---------------- Upiti / stanje ----------------

    @GetMapping("/state")
    public Map<String, Object> fullState() {
        Map<String, Object> s = state();
        s.put("factsByType", engine.getAllFactsByType());
        return s;
    }

    @GetMapping("/alerts")
    public Object alerts() {
        return engine.getAlerts();
    }

    @GetMapping("/severities")
    public Object severities() {
        return engine.getSeverities();
    }

    @GetMapping("/response-actions")
    public Object responseActions() {
        return engine.getResponseActions();
    }

    @GetMapping("/diagnose/{hostId}")
    public Map<String, Boolean> diagnose(@PathVariable String hostId) {
        return engine.diagnose(hostId);
    }

    @GetMapping("/why-not")
    public List<IncidentFactor> whyNot(@RequestParam String type, @RequestParam String host) {
        return engine.whyNotTriggered(type, host);
    }

    // ---------------- helpers ----------------

    private Map<String, Object> state() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("alerts", engine.getAlerts());
        s.put("severities", engine.getSeverities());
        s.put("responseActions", engine.getResponseActions());
        s.put("clock", engine.now());
        return s;
    }
}
