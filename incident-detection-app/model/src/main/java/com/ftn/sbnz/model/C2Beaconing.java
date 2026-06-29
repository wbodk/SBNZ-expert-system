package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (CEP) — regularni outbound zahtevi ka istom destIP konstantnim intervalom 30+ min (R3.9). */
public class C2Beaconing implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String destIp;

    public C2Beaconing() {
    }

    public C2Beaconing(String hostId, String destIp) {
        this.hostId = hostId;
        this.destIp = destIp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    @Override
    public String toString() {
        return "C2Beaconing{hostId=" + hostId + ", destIp=" + destIp + "}";
    }
}
