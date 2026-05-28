package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("30m")
public class MetricEvent implements Serializable {

    public enum MetricType {
        CPU_USAGE,
        MEMORY_USAGE,
        DISK_USAGE,
        NETWORK_TRAFFIC_MBPS,
        SERVICE_AVAILABILITY,
        SERVICE_RESPONSE_TIME_MS
    }

    private static final long serialVersionUID = 1L;

    private String hostId;
    private MetricType metricType;
    private double value;
    private Date timestamp;

    public MetricEvent() {
    }

    public MetricEvent(String hostId, MetricType metricType, double value, Date timestamp) {
        this.hostId = hostId;
        this.metricType = metricType;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MetricEvent{hostId=" + hostId + ", type=" + metricType + ", value=" + value + "}";
    }
}
