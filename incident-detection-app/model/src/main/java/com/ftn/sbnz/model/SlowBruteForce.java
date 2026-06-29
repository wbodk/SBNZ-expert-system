package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (accumulate) — failedLogins > 20 sa DISTINCT srcIP > 15 u 24h (R3.11). */
public class SlowBruteForce implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private long failedCount;
    private long distinctIps;

    public SlowBruteForce() {
    }

    public SlowBruteForce(String hostId, long failedCount, long distinctIps) {
        this.hostId = hostId;
        this.failedCount = failedCount;
        this.distinctIps = distinctIps;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public long getDistinctIps() {
        return distinctIps;
    }

    public void setDistinctIps(long distinctIps) {
        this.distinctIps = distinctIps;
    }

    @Override
    public String toString() {
        return "SlowBruteForce{hostId=" + hostId + ", failedCount=" + failedCount
                + ", distinctIps=" + distinctIps + "}";
    }
}
