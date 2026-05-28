package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("1h")
public class LoginAttemptEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;
    private String sourceIp;
    private boolean success;
    private Date timestamp;

    public LoginAttemptEvent() {
    }

    public LoginAttemptEvent(String hostId, String userId, String sourceIp, boolean success, Date timestamp) {
        this.hostId = hostId;
        this.userId = userId;
        this.sourceIp = sourceIp;
        this.success = success;
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

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoginAttemptEvent{hostId=" + hostId + ", userId=" + userId
                + ", sourceIp=" + sourceIp + ", success=" + success + "}";
    }
}
