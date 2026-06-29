package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (CEP, UBA) — UnusualLoginFrequency → SensitiveDataAccess within 1h, isti user (R3.13). */
public class InsiderThreat implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String hostId;

    public InsiderThreat() {
    }

    public InsiderThreat(String userId, String hostId) {
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
        return "InsiderThreat{userId=" + userId + ", hostId=" + hostId + "}";
    }
}
