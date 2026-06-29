package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Nivo 3 (CEP) — isti SuspiciousIP viđen na 3+ različita servisa za < 10 min (R3.6).
 * {@code hostId} je jedan reprezentativni servis (radi BC stabla po hostu).
 */
public class LateralMovement implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String sourceIp;
    private int serviceCount;

    public LateralMovement() {
    }

    public LateralMovement(String hostId, String sourceIp, int serviceCount) {
        this.hostId = hostId;
        this.sourceIp = sourceIp;
        this.serviceCount = serviceCount;
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

    public int getServiceCount() {
        return serviceCount;
    }

    public void setServiceCount(int serviceCount) {
        this.serviceCount = serviceCount;
    }

    @Override
    public String toString() {
        return "LateralMovement{hostId=" + hostId + ", sourceIp=" + sourceIp
                + ", serviceCount=" + serviceCount + "}";
    }
}
