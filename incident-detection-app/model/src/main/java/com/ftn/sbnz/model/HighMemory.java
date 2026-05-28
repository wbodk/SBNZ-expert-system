package com.ftn.sbnz.model;

import java.io.Serializable;

public class HighMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double value;

    public HighMemory() {
    }

    public HighMemory(String hostId, double value) {
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
        return "HighMemory{hostId=" + hostId + ", value=" + value + "}";
    }
}
