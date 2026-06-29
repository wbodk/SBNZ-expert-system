package com.ftn.sbnz.model;

import java.io.Serializable;

/** Nivo 3 (CEP) — MassFileModification + ServiceDown + HighCPU within 5 min, isti host (R3.10). */
public class RansomwareActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;

    public RansomwareActivity() {
    }

    public RansomwareActivity(String hostId) {
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
        return "RansomwareActivity{hostId=" + hostId + "}";
    }
}
