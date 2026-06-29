package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 — SensitiveDataAccess AND OutboundSpike (R2.7). */
public class SuspiciousDataMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public SuspiciousDataMovement() {
    }

    public SuspiciousDataMovement(String hostId) {
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
        return "SuspiciousDataMovement{hostId=" + hostId + "}";
    }
}
