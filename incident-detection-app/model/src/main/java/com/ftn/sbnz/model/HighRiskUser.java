package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (accumulate, UBA) — UserRiskScore > peerGroup.avgRisk × 2 (R3.12). */
public class HighRiskUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private double riskScore;

    public HighRiskUser() {
    }

    public HighRiskUser(String userId, double riskScore) {
        this.userId = userId;
        this.riskScore = riskScore;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    @Override
    public String toString() {
        return "HighRiskUser{userId=" + userId + ", riskScore=" + riskScore + "}";
    }
}
