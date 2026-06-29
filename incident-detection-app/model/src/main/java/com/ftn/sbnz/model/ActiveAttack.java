package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 — BruteForceAttempt AND SuspiciousIP (R2.3). */
public class ActiveAttack implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public ActiveAttack() {
    }

    public ActiveAttack(String hostId) {
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return "ActiveAttack{hostId=" + hostId + "}";
    }
}
