package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Agregatna statistika peer grupe (uloga/odeljenje) — prosečan risk score.
 * Koristi se za peer-group poređenje u R3.12.
 */
public class PeerGroupStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private String groupId;
    private double avgRisk;

    public PeerGroupStats() {
    }

    public PeerGroupStats(String groupId, double avgRisk) {
        this.groupId = groupId;
        this.avgRisk = avgRisk;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getAvgRisk() {
        return avgRisk;
    }

    public void setAvgRisk(double avgRisk) {
        this.avgRisk = avgRisk;
    }

    @Override
    public String toString() {
        return "PeerGroupStats{groupId=" + groupId + ", avgRisk=" + avgRisk + "}";
    }
}
