package com.ftn.sbnz.model;

import java.io.Serializable;
import java.util.Date;

import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

/**
 * Događaj nad fajl-sistemom — broj izmenjenih/kreiranih fajlova i ekstenzija.
 * Visoka stopa modifikacija pokreće R1.10 (MassFileModification); sumnjive
 * ekstenzije (.encrypted, .lock) pojačavaju ransomware indikator.
 */
@Role(Role.Type.EVENT)
@Timestamp("timestamp")
@Expires("10m")
public class FileEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hostId;
    private int fileCount;
    private String extension;
    private Date timestamp;

    public FileEvent() {
    }

    public FileEvent(String hostId, int fileCount, String extension, Date timestamp) {
        this.hostId = hostId;
        this.fileCount = fileCount;
        this.extension = extension;
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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FileEvent{hostId=" + hostId + ", fileCount=" + fileCount
                + ", extension=" + extension + "}";
    }
}
