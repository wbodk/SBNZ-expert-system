package com.ftn.sbnz.model;

import java.io.Serializable;

public class IncidentSeverity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private int score;

    public IncidentSeverity() {
    }

    public IncidentSeverity(String hostId, int score) {
        this.hostId = hostId;
        this.score = score;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void add(int delta) {
        this.score += delta;
    }

    @Override
    public String toString() {
        return "IncidentSeverity{hostId=" + hostId + ", score=" + score + "}";
    }
}
