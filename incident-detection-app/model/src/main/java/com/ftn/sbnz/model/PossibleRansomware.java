package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 2 — MassFileModification AND ServiceDown (R2.9). */
public class PossibleRansomware implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public PossibleRansomware() {
    }

    public PossibleRansomware(String hostId) {
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
        return "PossibleRansomware{hostId=" + hostId + "}";
    }
}
