package com.ftn.sbnz.model;

import java.io.Serializable;

public class PotentialDDoS implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public PotentialDDoS() {
    }

    public PotentialDDoS(String hostId) {
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
        return "PotentialDDoS{hostId=" + hostId + "}";
    }
}
