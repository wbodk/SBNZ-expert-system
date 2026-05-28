package com.ftn.sbnz.model;

import java.io.Serializable;

public class Host implements Serializable {

    public enum Tier {
        TIER_1, TIER_2, TIER_3
    }

    private static final long serialVersionUID = 1L;

    private String id;
    private String hostname;
    private Tier tier;

    public Host() {
    }

    public Host(String id, String hostname, Tier tier) {
        this.id = id;
        this.hostname = hostname;
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return "Host{id=" + id + ", hostname=" + hostname + ", tier=" + tier + "}";
    }
}
