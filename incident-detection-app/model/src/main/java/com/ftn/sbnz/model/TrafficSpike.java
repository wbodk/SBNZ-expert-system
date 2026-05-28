package com.ftn.sbnz.model;

import java.io.Serializable;

public class TrafficSpike implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double currentMbps;
    private double baselineMbps;

    public TrafficSpike() {
    }

    public TrafficSpike(String hostId, double currentMbps, double baselineMbps) {
        this.hostId = hostId;
        this.currentMbps = currentMbps;
        this.baselineMbps = baselineMbps;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getCurrentMbps() {
        return currentMbps;
    }

    public void setCurrentMbps(double currentMbps) {
        this.currentMbps = currentMbps;
    }

    public double getBaselineMbps() {
        return baselineMbps;
    }

    public void setBaselineMbps(double baselineMbps) {
        this.baselineMbps = baselineMbps;
    }

    @Override
    public String toString() {
        return "TrafficSpike{hostId=" + hostId + ", current=" + currentMbps
                + ", baseline=" + baselineMbps + "}";
    }
}
