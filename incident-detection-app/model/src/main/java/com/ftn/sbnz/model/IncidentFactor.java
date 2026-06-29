package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * List u stablu zavisnosti incidenata (backward chaining). Predstavlja
 * konkretnu činjenicu na hostu (npr. "web-01 ima HighCPU"). {@code id} je
 * logičko ime čvora (npr. "HighCPU", "DDoSPattern"), a {@code satisfied}
 * označava da li je uslov ispunjen. Koristi se u query-jima Q1–Q8.
 */
public class IncidentFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String hostId;
    private boolean satisfied;
    private double confidence;

    public IncidentFactor() {
    }

    public IncidentFactor(String id, String hostId, boolean satisfied, double confidence) {
        this.id = id;
        this.hostId = hostId;
        this.satisfied = satisfied;
        this.confidence = confidence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "IncidentFactor{id=" + id + ", hostId=" + hostId
                + ", satisfied=" + satisfied + ", confidence=" + confidence + "}";
    }
}
