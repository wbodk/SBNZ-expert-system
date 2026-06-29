package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Nivo 1 (UBA) — userLoginCount > baseline.avgLogins × 3 (R1.12). EVENT —
 * učestvuje u CEP korelaciji R3.13 (UnusualLoginFrequency → SensitiveDataAccess).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("2h")
public class UnusualLoginFrequency implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String hostId;
    private int loginCount;
    private Date timestamp;

    public UnusualLoginFrequency() {
    }

    public UnusualLoginFrequency(String userId, String hostId, int loginCount, Date timestamp) {
        this.userId = userId;
        this.hostId = hostId;
        this.loginCount = loginCount;
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

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UnusualLoginFrequency{userId=" + userId + ", hostId=" + hostId
                + ", loginCount=" + loginCount + "}";
    }
}
