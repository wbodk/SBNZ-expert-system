package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Tekući risk score korisnika. Poredi se sa prosekom peer grupe
 * ({@link PeerGroupStats}) u R3.12 radi detekcije {@link HighRiskUser}.
 */
public class UserRiskScore implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private double value;

    public UserRiskScore() {
    }

    public UserRiskScore(String userId, double value) {
        this.userId = userId;
        this.value = value;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void add(double delta) {
        this.value += delta;
    }

    @Override
    public String toString() {
        return "UserRiskScore{userId=" + userId + ", value=" + value + "}";
    }
}
