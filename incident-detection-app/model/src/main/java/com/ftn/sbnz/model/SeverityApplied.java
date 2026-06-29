package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Pomoćni marker za idempotentnost: označava da je određeno pravilo
 * ({@code ruleId}) već dodalo svoj severity doprinos za dati host. Koriste ga
 * pravila koja menjaju severity bez umetanja sopstvene izvedene činjenice
 * (npr. R2.5, R4.1, R4.2, R4.9), da se doprinos ne bi primenio više puta.
 */
public class SeverityApplied implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String ruleId;

    public SeverityApplied() {
    }

    public SeverityApplied(String hostId, String ruleId) {
        this.hostId = hostId;
        this.ruleId = ruleId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String toString() {
        return "SeverityApplied{hostId=" + hostId + ", ruleId=" + ruleId + "}";
    }
}
