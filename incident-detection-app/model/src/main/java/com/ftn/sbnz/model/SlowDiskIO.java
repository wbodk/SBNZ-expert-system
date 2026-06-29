package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 1 — diskIOLatency > 500ms for > 3 min (R1.14). */
public class SlowDiskIO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double latencyMs;

    public SlowDiskIO() {
    }

    public SlowDiskIO(String hostId, double latencyMs) {
        this.hostId = hostId;
        this.latencyMs = latencyMs;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(double latencyMs) {
        this.latencyMs = latencyMs;
    }

    @Override
    public String toString() {
        return "SlowDiskIO{hostId=" + hostId + ", latencyMs=" + latencyMs + "}";
    }
}
