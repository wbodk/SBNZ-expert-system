package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 1 — serviceResponseTime > 3000ms (R1.4). */
public class SlowService implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double responseTimeMs;

    public SlowService() {
    }

    public SlowService(String hostId, double responseTimeMs) {
        this.hostId = hostId;
        this.responseTimeMs = responseTimeMs;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(double responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    @Override
    public String toString() {
        return "SlowService{hostId=" + hostId + ", responseTimeMs=" + responseTimeMs + "}";
    }
}
