package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Nivo 1 — outbound saobraćaj > 5× outboundBaseline (R1.9). EVENT — učestvuje
 * u CEP korelaciji R3.8 (SensitiveDataAccess → OutboundSpike).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("1h")
public class OutboundSpike implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double currentMb;
    private double baselineMb;
    private Date timestamp;

    public OutboundSpike() {
    }

    public OutboundSpike(String hostId, double currentMb, double baselineMb, Date timestamp) {
        this.hostId = hostId;
        this.currentMb = currentMb;
        this.baselineMb = baselineMb;
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getCurrentMb() {
        return currentMb;
    }

    public void setCurrentMb(double currentMb) {
        this.currentMb = currentMb;
    }

    public double getBaselineMb() {
        return baselineMb;
    }

    public void setBaselineMb(double baselineMb) {
        this.baselineMb = baselineMb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OutboundSpike{hostId=" + hostId + ", currentMb=" + currentMb
                + ", baselineMb=" + baselineMb + "}";
    }
}
