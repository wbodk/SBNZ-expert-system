package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 (UBA) — UnusualLoginFrequency AND UnusualDataVolume (R2.8). */
public class SuspiciousUserActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String hostId;

    public SuspiciousUserActivity() {
    }

    public SuspiciousUserActivity(String userId, String hostId) {
        this.userId = userId;
        this.hostId = hostId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return "SuspiciousUserActivity{userId=" + userId + ", hostId=" + hostId + "}";
    }
}
