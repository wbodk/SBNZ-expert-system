package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Agregat dnevne aktivnosti korisnika — dnevni broj logina i obim preuzetih
 * podataka. Poredi se sa {@link UserActivityProfile} u pravilima R1.12 i R1.13.
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("25h")
public class UserActivityEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String hostId;
    private int dailyLoginCount;
    private double dataDownloadVolumeMb;
    private int activityHour;
    private Date timestamp;

    public UserActivityEvent() {
    }

    public UserActivityEvent(String userId, String hostId, int dailyLoginCount,
                             double dataDownloadVolumeMb, int activityHour, Date timestamp) {
        this.userId = userId;
        this.hostId = hostId;
        this.dailyLoginCount = dailyLoginCount;
        this.dataDownloadVolumeMb = dataDownloadVolumeMb;
        this.activityHour = activityHour;
        this.timestamp = timestamp;
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

    public int getDailyLoginCount() {
        return dailyLoginCount;
    }

    public void setDailyLoginCount(int dailyLoginCount) {
        this.dailyLoginCount = dailyLoginCount;
    }

    public double getDataDownloadVolumeMb() {
        return dataDownloadVolumeMb;
    }

    public void setDataDownloadVolumeMb(double dataDownloadVolumeMb) {
        this.dataDownloadVolumeMb = dataDownloadVolumeMb;
    }

    public int getActivityHour() {
        return activityHour;
    }

    public void setActivityHour(int activityHour) {
        this.activityHour = activityHour;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserActivityEvent{userId=" + userId + ", hostId=" + hostId
                + ", dailyLoginCount=" + dailyLoginCount
                + ", dataDownloadVolumeMb=" + dataDownloadVolumeMb + ", activityHour=" + activityHour + "}";
    }
}
