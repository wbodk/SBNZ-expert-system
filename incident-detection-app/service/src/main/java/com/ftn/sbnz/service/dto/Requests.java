package com.ftn.sbnz.service.dto;

import com.ftn.sbnz.model.MetricEvent.MetricType;

/**
 * Request DTO-i za REST sloj. Eventi se ne primaju sa timestamp-om — engine im
 * dodeljuje vreme pseudo-sata pri ubacivanju (tako ostaju usklađeni sa CEP-om).
 */
public class Requests {

    public static class MetricEventRequest {
        public String hostId;
        public MetricType metricType;
        public double value;
    }

    public static class LoginEventRequest {
        public String hostId;
        public String userId;
        public String sourceIp;
        public boolean success;
        public int loginHour = -1;
    }

    public static class ResourceAccessRequest {
        public String hostId;
        public String userId;
        public String resourceId;
        public int accessHour;
    }

    public static class UserActivityRequest {
        public String userId;
        public String hostId;
        public int dailyLoginCount;
        public double dataDownloadVolumeMb;
        public int activityHour;
    }

    public static class FileEventRequest {
        public String hostId;
        public int fileCount;
        public String extension;
    }

    public static class NetworkEventRequest {
        public String hostId;
        public String destIp;
        public double bytesOutMb;
    }

    public static class ClockAdvanceRequest {
        public long amount;
        public String unit = "MINUTES"; // SECONDS / MINUTES / HOURS / DAYS
    }
}
