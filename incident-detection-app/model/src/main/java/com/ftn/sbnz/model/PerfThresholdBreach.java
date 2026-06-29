package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Prekoračenje praga performansi specifičnog za klasu servisa (web/db/cache/batch).
 * Generišu ga pravila iz PerServiceThresholds.xls decision table (Templates, Avanesov).
 */
public class PerfThresholdBreach implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String serviceClass;
    private double value;

    public PerfThresholdBreach() {
    }

    public PerfThresholdBreach(String hostId, String serviceClass, double value) {
        this.hostId = hostId;
        this.serviceClass = serviceClass;
        this.value = value;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PerfThresholdBreach{hostId=" + hostId + ", serviceClass=" + serviceClass
                + ", value=" + value + "}";
    }
}
