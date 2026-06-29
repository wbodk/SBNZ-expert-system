package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (CEP) — SensitiveDataAccess → OutboundSpike within 15 min, isti host (R3.8). */
public class DataExfiltration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;

    public DataExfiltration() {
    }

    public DataExfiltration(String hostId, String userId) {
        this.hostId = hostId;
        this.userId = userId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DataExfiltration{hostId=" + hostId + ", userId=" + userId + "}";
    }
}
