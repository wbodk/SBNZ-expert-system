package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Konfiguraciona činjenica: na hostu je u toku planirano održavanje.
 * Koristi se kao negativni uslov u R2.5 (ServiceDown AND NOT ScheduledMaintenance).
 */
public class ScheduledMaintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public ScheduledMaintenance() {
    }

    public ScheduledMaintenance(String hostId) {
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return "ScheduledMaintenance{hostId=" + hostId + "}";
    }
}
