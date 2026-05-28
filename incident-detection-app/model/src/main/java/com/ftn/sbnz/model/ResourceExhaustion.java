package com.ftn.sbnz.model;

import java.io.Serializable;

public class ResourceExhaustion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public ResourceExhaustion() {
    }

    public ResourceExhaustion(String hostId) {
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
        return "ResourceExhaustion{hostId=" + hostId + "}";
    }
}
