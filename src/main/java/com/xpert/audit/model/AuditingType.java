package com.xpert.audit.model;

/**
 * Possible type to audit events
 *
 * @author Ayslan
 */
public enum AuditingType {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private String description;

    AuditingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
