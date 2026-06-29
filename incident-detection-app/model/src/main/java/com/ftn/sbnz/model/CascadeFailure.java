package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (accumulate) — COUNT(ServiceDown) over 5 min > 3 (R3.3). */
public class CascadeFailure implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private long downCount;

    public CascadeFailure() {
    }

    public CascadeFailure(String hostId, long downCount) {
        this.hostId = hostId;
        this.downCount = downCount;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public long getDownCount() {
        return downCount;
    }

    public void setDownCount(long downCount) {
        this.downCount = downCount;
    }

    @Override
    public String toString() {
        return "CascadeFailure{hostId=" + hostId + ", downCount=" + downCount + "}";
    }
}
