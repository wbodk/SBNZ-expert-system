package com.ftn.sbnz.model;

import java.io.Serializable;

/**
 * Grana u stablu zavisnosti incidenata (backward chaining): roditeljski čvor
 * {@code parentId} zavisi od dečjeg čvora {@code childId}. Stablo je statički
 * definisano (seed) i prolazi se rekurzivnim query-jima Q1–Q8.
 */
public class IncidentDependency implements Serializable {

    private static final long serialVersionUID = 1L;

    private String parentId;
    private String childId;

    public IncidentDependency() {
    }

    public IncidentDependency(String parentId, String childId) {
        this.parentId = parentId;
        this.childId = childId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "IncidentDependency{parentId=" + parentId + ", childId=" + childId + "}";
    }
}
