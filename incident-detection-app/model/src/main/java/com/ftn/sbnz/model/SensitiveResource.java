package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Registar osetljivih resursa (fajl-share-ovi, DB tabele, endpoint-i).
 * Pristup resursu iz ovog registra pokreće R1.11 (SensitiveDataAccess).
 */
public class SensitiveResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private String resourceId;

    public SensitiveResource() {
    }

    public SensitiveResource(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "SensitiveResource{resourceId=" + resourceId + "}";
    }
}
