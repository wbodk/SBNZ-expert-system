package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Nivo 1 — fileModificationRate > 100 files/min (R1.10). EVENT — učestvuje u
 * CEP korelaciji R3.10 (MassFileModification + ServiceDown + HighCPU).
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("30m")
public class MassFileModification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private int fileCount;
    private Date timestamp;

    public MassFileModification() {
    }

    public MassFileModification(String hostId, int fileCount, Date timestamp) {
        this.hostId = hostId;
        this.fileCount = fileCount;
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MassFileModification{hostId=" + hostId + ", fileCount=" + fileCount + "}";
    }
}
