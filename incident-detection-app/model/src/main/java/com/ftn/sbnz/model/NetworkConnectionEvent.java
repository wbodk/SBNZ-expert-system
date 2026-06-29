package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Odlazna (outbound) mrežna konekcija ka {@code destIp}. Akumulira se za
 * R1.9 (OutboundSpike) i analizira na regularne intervale za R3.9 (C2Beaconing).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("1h")
public class NetworkConnectionEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String destIp;
    private double bytesOutMb;
    private Date timestamp;

    public NetworkConnectionEvent() {
    }

    public NetworkConnectionEvent(String hostId, String destIp, double bytesOutMb, Date timestamp) {
        this.hostId = hostId;
        this.destIp = destIp;
        this.bytesOutMb = bytesOutMb;
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public double getBytesOutMb() {
        return bytesOutMb;
    }

    public void setBytesOutMb(double bytesOutMb) {
        this.bytesOutMb = bytesOutMb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NetworkConnectionEvent{hostId=" + hostId + ", destIp=" + destIp
                + ", bytesOutMb=" + bytesOutMb + "}";
    }
}
