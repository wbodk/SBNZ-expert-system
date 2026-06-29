package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 1 — diskUsage > 90% (R1.3). */
public class HighDisk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double value;

    public HighDisk() {
    }

    public HighDisk(String hostId, double value) {
        this.hostId = hostId;
        this.value = value;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "HighDisk{hostId=" + hostId + ", value=" + value + "}";
    }
}
