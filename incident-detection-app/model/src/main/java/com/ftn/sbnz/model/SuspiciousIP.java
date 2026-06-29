package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 1 — sourceIP iz blacklist-e (R1.7). hostId je servis na kom je viđen. */
public class SuspiciousIP implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String sourceIp;

    public SuspiciousIP() {
    }

    public SuspiciousIP(String hostId, String sourceIp) {
        this.hostId = hostId;
        this.sourceIp = sourceIp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    @Override
    public String toString() {
        return "SuspiciousIP{hostId=" + hostId + ", sourceIp=" + sourceIp + "}";
    }
}
