package com.ftn.sbnz.model;

import java.io.Serializable;

public class SustainedHighLoad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double avgValue;

    public SustainedHighLoad() {
    }

    public SustainedHighLoad(String hostId, double avgValue) {
        this.hostId = hostId;
        this.avgValue = avgValue;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getAvgValue() {
        return avgValue;
    }

    public void setAvgValue(double avgValue) {
        this.avgValue = avgValue;
    }

    @Override
    public String toString() {
        return "SustainedHighLoad{hostId=" + hostId + ", avgValue=" + avgValue + "}";
    }
}
