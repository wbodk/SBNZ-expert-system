package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (accumulate) — COUNT(DISTINCT srcIP) over 1 min > 500 (R3.7). */
public class DistributedAttack implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private long distinctSources;

    public DistributedAttack() {
    }

    public DistributedAttack(String hostId, long distinctSources) {
        this.hostId = hostId;
        this.distinctSources = distinctSources;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public long getDistinctSources() {
        return distinctSources;
    }

    public void setDistinctSources(long distinctSources) {
        this.distinctSources = distinctSources;
    }

    @Override
    public String toString() {
        return "DistributedAttack{hostId=" + hostId + ", distinctSources=" + distinctSources + "}";
    }
}
