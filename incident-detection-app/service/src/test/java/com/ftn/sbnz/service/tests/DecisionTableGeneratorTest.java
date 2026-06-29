package com.ftn.sbnz.service.tests;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

/**
 * Generator XLS decision table-a (Templates tehnika — Avanesov).
 *
 * Pošto u okruženju nema alata za ručno autorovanje .xls binarnih fajlova,
 * tabele se programski generišu Apache POI-jem (koji dolazi uz
 * drools-decisiontables) i upisuju u resurse `rules` modula. Pravila iz tabela
 * Drools auto-kompajlira pri učitavanju kbase-a.
 *
 * Layout decision table-a (0-based redovi posle "RuleTable"):
 *   r4: tipovi kolona (CONDITION/ACTION/PRIORITY/NO-LOOP)
 *   r5: deklaracije objekata (npr. "$h : Host()")
 *   r6: snippet-i sa $param
 *   r7: labele kolona
 *   r8+: podaci
 *
 * Upisuje samo ako fajl ne postoji (idempotentno). Za regenerisanje obrisati
 * fajlove u rules/src/main/resources/rules/templates/.
 */
public class DecisionTableGeneratorTest {

    private static final String DIR = "../rules/src/main/resources/rules/templates";

    private void set(Sheet s, int rowIdx, int colIdx, String value) {
        Row row = s.getRow(rowIdx);
        if (row == null) {
            row = s.createRow(rowIdx);
        }
        row.createCell(colIdx).setCellValue(value);
    }

    private void setRow(Sheet s, int rowIdx, String... values) {
        for (int c = 0; c < values.length; c++) {
            if (values[c] != null && !values[c].isEmpty()) {
                set(s, rowIdx, c, values[c]);
            }
        }
    }

    private void writeIfAbsent(String fileName, HSSFWorkbook wb) throws Exception {
        File dir = new File(DIR);
        dir.mkdirs();
        File f = new File(dir, fileName);
        if (f.exists()) {
            System.out.println("[DT-GEN] postoji, preskačem: " + f.getPath());
            wb.close();
            return;
        }
        try (FileOutputStream out = new FileOutputStream(f)) {
            wb.write(out);
        }
        wb.close();
        System.out.println("[DT-GEN] kreiran: " + f.getCanonicalPath());
    }

    // =================================================================
    // AssetCriticality.xls — Tier -> severity multiplier + routing (R4.12/R4.13)
    // =================================================================
    @Test
    public void generateAssetCriticality() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        Sheet s = wb.createSheet("AssetCriticality");

        set(s, 0, 0, "RuleSet");
        set(s, 0, 1, "rules.templates");
        set(s, 1, 0, "Import");
        set(s, 1, 1, "com.ftn.sbnz.model.Host,com.ftn.sbnz.model.Host.Tier,"
                + "com.ftn.sbnz.model.IncidentSeverity,com.ftn.sbnz.model.SeverityApplied");

        set(s, 3, 0, "RuleTable AssetCriticality");
        // marker SeverityApplied("TIER_MULT") garantuje da se multiplikator primeni tačno jednom
        setRow(s, 4, "CONDITION", "CONDITION", "CONDITION", "ACTION", "ACTION", "ACTION", "PRIORITY");
        setRow(s, 5, "$host : Host()", "$sev : IncidentSeverity()", "not SeverityApplied()", "", "", "", "");
        setRow(s, 6,
                "$hid : id, tier == Tier.$param",
                "hostId == $hid, score > $param",
                "hostId == $hid, ruleId == \"$param\"",
                "modify($sev){ setScore((int)($sev.getScore() * $param)) }",
                "insert(new SeverityApplied($hid, \"$param\"));",
                "System.out.println(\"[XLS AssetCriticality] \" + $hid + \" routing: $param\");",
                "");
        setRow(s, 7, "Tier", "Severity>0", "NotApplied", "Multiplier", "Marker", "Routing", "Priority");
        String[][] data = {
            {"TIER_1", "0", "TIER_MULT", "1.5", "TIER_MULT", "management+oncall", "-50"},
            {"TIER_2", "0", "TIER_MULT", "1.0", "TIER_MULT", "oncall",            "-50"},
            {"TIER_3", "0", "TIER_MULT", "0.7", "TIER_MULT", "oncall-only",       "-50"},
        };
        int r = 8;
        for (String[] row : data) { setRow(s, r++, row); }
        writeIfAbsent("AssetCriticality.xls", wb);
    }

    // =================================================================
    // PerServiceThresholds.xls — per-klasa CPU prag -> PerfThresholdBreach (+15)
    // =================================================================
    @Test
    public void generatePerServiceThresholds() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        Sheet s = wb.createSheet("PerServiceThresholds");

        set(s, 0, 0, "RuleSet");
        set(s, 0, 1, "rules.templates");
        set(s, 1, 0, "Import");
        set(s, 1, 1, "com.ftn.sbnz.model.Host,com.ftn.sbnz.model.MetricEvent,"
                + "com.ftn.sbnz.model.MetricEvent.MetricType,com.ftn.sbnz.model.IncidentSeverity,"
                + "com.ftn.sbnz.model.PerfThresholdBreach");

        set(s, 3, 0, "RuleTable PerServiceThresholds");
        setRow(s, 4, "CONDITION", "CONDITION", "CONDITION", "CONDITION", "ACTION", "ACTION", "NO-LOOP");
        setRow(s, 5, "$host : Host()", "$m : MetricEvent()", "$sev : IncidentSeverity()",
                "not PerfThresholdBreach()", "", "", "");
        setRow(s, 6,
                "$hid : id, serviceClass == \"$param\"",
                "hostId == $hid, metricType == MetricType.CPU_USAGE, value > $param",
                "hostId == $hid, score >= $param",
                "hostId == $hid, serviceClass == \"$param\"",
                "insert(new PerfThresholdBreach($hid, \"$param\", $m.getValue()));",
                "modify($sev){ add($param) }",
                "");
        setRow(s, 7, "ServiceClass", "CpuThreshold", "SevGuard", "NotBreached", "Breach", "AddSeverity", "NoLoop");
        String[][] data = {
            {"web",   "85", "0", "web",   "web",   "15", "true"},
            {"db",    "80", "0", "db",    "db",    "15", "true"},
            {"cache", "75", "0", "cache", "cache", "15", "true"},
            {"batch", "95", "0", "batch", "batch", "15", "true"},
        };
        int r = 8;
        for (String[] row : data) { setRow(s, r++, row); }
        writeIfAbsent("PerServiceThresholds.xls", wb);
    }

    // =================================================================
    // CompliancePolicy.xls — compliance tag -> notifyCompliance (posle alerta)
    // =================================================================
    @Test
    public void generateCompliancePolicy() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        Sheet s = wb.createSheet("CompliancePolicy");

        set(s, 0, 0, "RuleSet");
        set(s, 0, 1, "rules.templates");
        set(s, 1, 0, "Import");
        set(s, 1, 1, "com.ftn.sbnz.model.Host,com.ftn.sbnz.model.Alert,com.ftn.sbnz.model.ResponseAction");

        set(s, 3, 0, "RuleTable CompliancePolicy");
        setRow(s, 4, "CONDITION", "CONDITION", "CONDITION", "ACTION", "ACTION", "PRIORITY", "NO-LOOP");
        setRow(s, 5, "$host : Host()", "$a : Alert()", "not ResponseAction()", "", "", "", "");
        setRow(s, 6,
                "$hid : id, complianceTag == \"$param\"",
                "hostId == $hid, severity >= $param",
                "hostId == $hid, action == \"$param\"",
                "insert(new ResponseAction($hid, \"$param\", \"compliance notification\"));",
                "System.out.println(\"[XLS CompliancePolicy] \" + $hid + \" retention: $param\");",
                "", "");
        setRow(s, 7, "ComplianceTag", "AlertExists", "NotNotified", "NotifyAction", "Retention", "Priority", "NoLoop");
        String[][] data = {
            {"PCI",   "0", "notifyCompliance_PCI",   "notifyCompliance_PCI",   "7-years", "-200", "true"},
            {"HIPAA", "0", "notifyCompliance_HIPAA", "notifyCompliance_HIPAA", "6-years", "-200", "true"},
            {"SOX",   "0", "notifyCompliance_SOX",   "notifyCompliance_SOX",   "7-years", "-200", "true"},
        };
        int r = 8;
        for (String[] row : data) { setRow(s, r++, row); }
        writeIfAbsent("CompliancePolicy.xls", wb);
    }
}
