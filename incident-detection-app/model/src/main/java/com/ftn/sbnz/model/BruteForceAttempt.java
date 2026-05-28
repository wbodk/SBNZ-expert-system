package com.ftn.sbnz.model;

import java.io.Serializable;

public class BruteForceAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private long failedCount;

    public BruteForceAttempt() {
    }

    public BruteForceAttempt(String hostId, long failedCount) {
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
        return "BruteForceAttempt{hostId=" + hostId + ", failedCount=" + failedCount + "}";
    }
}
