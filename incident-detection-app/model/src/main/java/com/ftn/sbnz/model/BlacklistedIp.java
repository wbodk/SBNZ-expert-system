package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Registar poznatih malicioznih IP adresa (blacklist). Login/konekcija sa
 * adrese iz ovog registra pokreće R1.7 (SuspiciousIP).
 */
public class BlacklistedIp implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;

    public BlacklistedIp() {
    }

    public BlacklistedIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "BlacklistedIp{ip=" + ip + "}";
    }
}
