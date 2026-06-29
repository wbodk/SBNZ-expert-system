package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 — SensitiveDataAccess AND loginHour van dozvoljenih sati (R2.10). */
public class SuspiciousAfterHoursAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;

    public SuspiciousAfterHoursAccess() {
    }

    public SuspiciousAfterHoursAccess(String hostId, String userId) {
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
        return "SuspiciousAfterHoursAccess{hostId=" + hostId + ", userId=" + userId + "}";
    }
}
