package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Konfiguraciona činjenica: normalni (baseline) saobraćaj po hostu.
 * Koristi se za R1.8 (TrafficSpike, 10×) i R1.9 (OutboundSpike, 5×).
 */
public class HostBaseline implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private double networkBaselineMbps;
    private double outboundBaselineMb;

    public HostBaseline() {
    }

    public HostBaseline(String hostId, double networkBaselineMbps, double outboundBaselineMb) {
        this.hostId = hostId;
        this.networkBaselineMbps = networkBaselineMbps;
        this.outboundBaselineMb = outboundBaselineMb;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public double getNetworkBaselineMbps() {
        return networkBaselineMbps;
    }

    public void setNetworkBaselineMbps(double networkBaselineMbps) {
        this.networkBaselineMbps = networkBaselineMbps;
    }

    public double getOutboundBaselineMb() {
        return outboundBaselineMb;
    }

    public void setOutboundBaselineMb(double outboundBaselineMb) {
        this.outboundBaselineMb = outboundBaselineMb;
    }

    @Override
    public String toString() {
        return "HostBaseline{hostId=" + hostId + ", networkBaselineMbps=" + networkBaselineMbps
                + ", outboundBaselineMb=" + outboundBaselineMb + "}";
    }
}
