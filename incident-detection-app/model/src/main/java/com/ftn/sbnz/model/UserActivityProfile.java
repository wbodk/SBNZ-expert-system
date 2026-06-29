package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Per-user baseline (UBA): prosečan dnevni broj logina, prosečan obim podataka,
 * dozvoljeno radno vreme i peer grupa. Akumulira se kroz vreme (R3.14), a
 * koristi u R1.12, R1.13, R2.10 i za peer-group poređenje (R3.12).
 */
public class UserActivityProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private double avgDailyLogins;
    private double avgDataVolumeMb;
    private int allowedHourStart;
    private int allowedHourEnd;
    private String peerGroup;

    public UserActivityProfile() {
    }

    public UserActivityProfile(String userId, double avgDailyLogins, double avgDataVolumeMb,
                               int allowedHourStart, int allowedHourEnd, String peerGroup) {
        this.userId = userId;
        this.avgDailyLogins = avgDailyLogins;
        this.avgDataVolumeMb = avgDataVolumeMb;
        this.allowedHourStart = allowedHourStart;
        this.allowedHourEnd = allowedHourEnd;
        this.peerGroup = peerGroup;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAvgDailyLogins() {
        return avgDailyLogins;
    }

    public void setAvgDailyLogins(double avgDailyLogins) {
        this.avgDailyLogins = avgDailyLogins;
    }

    public double getAvgDataVolumeMb() {
        return avgDataVolumeMb;
    }

    public void setAvgDataVolumeMb(double avgDataVolumeMb) {
        this.avgDataVolumeMb = avgDataVolumeMb;
    }

    public int getAllowedHourStart() {
        return allowedHourStart;
    }

    public void setAllowedHourStart(int allowedHourStart) {
        this.allowedHourStart = allowedHourStart;
    }

    public int getAllowedHourEnd() {
        return allowedHourEnd;
    }

    public void setAllowedHourEnd(int allowedHourEnd) {
        this.allowedHourEnd = allowedHourEnd;
    }

    public String getPeerGroup() {
        return peerGroup;
    }

    public void setPeerGroup(String peerGroup) {
        this.peerGroup = peerGroup;
    }

    @Override
    public String toString() {
        return "UserActivityProfile{userId=" + userId + ", avgDailyLogins=" + avgDailyLogins
                + ", avgDataVolumeMb=" + avgDataVolumeMb + ", allowedHours=" + allowedHourStart
                + "-" + allowedHourEnd + ", peerGroup=" + peerGroup + "}";
    }
}
