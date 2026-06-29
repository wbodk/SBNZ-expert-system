package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 — ServiceDown AND HighDisk (R2.2). */
public class StorageIncident implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public StorageIncident() {
    }

    public StorageIncident(String hostId) {
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
        return "StorageIncident{hostId=" + hostId + "}";
    }
}
