package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Nivo 1 — pristup resursu iz sensitive registra (R1.11). EVENT — učestvuje u
 * CEP korelacijama R3.8 (→ OutboundSpike) i R3.13 (UnusualLoginFrequency →).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("2h")
public class SensitiveDataAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;
    private String resourceId;
    private int accessHour;
    private Date timestamp;

    public SensitiveDataAccess() {
    }

    public SensitiveDataAccess(String hostId, String userId, String resourceId, int accessHour, Date timestamp) {
        this.hostId = hostId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.accessHour = accessHour;
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getAccessHour() {
        return accessHour;
    }

    public void setAccessHour(int accessHour) {
        this.accessHour = accessHour;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensitiveDataAccess{hostId=" + hostId + ", userId=" + userId
                + ", resourceId=" + resourceId + "}";
    }
}
