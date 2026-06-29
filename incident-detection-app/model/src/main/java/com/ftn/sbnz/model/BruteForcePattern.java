package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (accumulate) — COUNT(failedLogins) over 10 min > 50 (R3.1). */
public class BruteForcePattern implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private long failedCount;

    public BruteForcePattern() {
    }

    public BruteForcePattern(String hostId, long failedCount) {
        this.hostId = hostId;
        this.failedCount = failedCount;
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

    @Override
    public String toString() {
        return "BruteForcePattern{hostId=" + hostId + ", failedCount=" + failedCount + "}";
    }
}
