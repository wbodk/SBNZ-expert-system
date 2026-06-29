package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Nivo 2 — loginHour van dozvoljenih sati AND failedLogins > 0 (R2.6).
 * EVENT — učestvuje u CEP korelaciji R3.5 (BruteForceAttempt → AnomalousAccess).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("1h")
public class AnomalousAccess implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String userId;
    private Date timestamp;

    public AnomalousAccess() {
    }

    public AnomalousAccess(String hostId, String userId, Date timestamp) {
        this.hostId = hostId;
        this.userId = userId;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AnomalousAccess{hostId=" + hostId + ", userId=" + userId + "}";
    }
}
