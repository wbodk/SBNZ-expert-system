package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (CEP) — BruteForceAttempt → AnomalousAccess within 5 min (R3.5). */
public class CompromisedAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;

    public CompromisedAccount() {
    }

    public CompromisedAccount(String hostId, String userId) {
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
        return "CompromisedAccount{hostId=" + hostId + ", userId=" + userId + "}";
    }
}
