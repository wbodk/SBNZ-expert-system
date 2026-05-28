package com.ftn.sbnz.model;

import java.io.Serializable;

public class Alert implements Serializable {

    public enum Level {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    private static final long serialVersionUID = 1L;

    private String hostId;
    private Level level;
    private int severity;
    private String description;

    public Alert() {
    }

    public Alert(String hostId, Level level, int severity, String description) {
        this.hostId = hostId;
        this.level = level;
        this.severity = severity;
        this.description = description;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Alert{hostId=" + hostId + ", level=" + level
                + ", severity=" + severity + ", description='" + description + "'}";
    }
}
