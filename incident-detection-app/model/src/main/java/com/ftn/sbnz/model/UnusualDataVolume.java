package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 1 (UBA) — userDataDownloadVolume > baseline.avgVolume × 5 (R1.13). */
public class UnusualDataVolume implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String hostId;
    private double volumeMb;

    public UnusualDataVolume() {
    }

    public UnusualDataVolume(String userId, String hostId, double volumeMb) {
        this.userId = userId;
        this.hostId = hostId;
        this.volumeMb = volumeMb;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getVolumeMb() {
        return volumeMb;
    }

    public void setVolumeMb(double volumeMb) {
        this.volumeMb = volumeMb;
    }

    @Override
    public String toString() {
        return "UnusualDataVolume{userId=" + userId + ", hostId=" + hostId
                + ", volumeMb=" + volumeMb + "}";
    }
}
