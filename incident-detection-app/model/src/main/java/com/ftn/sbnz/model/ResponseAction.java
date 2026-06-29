package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Nivo 4 — automatska odbrambena akcija koju je pravilo pokrenulo
 * (isolateHost, blockOutboundTraffic, revokeAccess, ...). Eksplicitna
 * činjenica radi objašnjivosti i prikaza u klijentskoj aplikaciji.
 */
public class ResponseAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private String action;
    private String detail;

    public ResponseAction() {
    }

    public ResponseAction(String hostId, String action, String detail) {
        this.hostId = hostId;
        this.action = action;
        this.detail = detail;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ResponseAction{hostId=" + hostId + ", action=" + action + ", detail='" + detail + "'}";
    }
}
