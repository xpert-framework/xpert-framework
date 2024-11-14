package com.xpert.audit.model;

/**
 * Possible type to audit events
 *
 * @author ayslan
 */
public enum AuditingType {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private final String description;

    AuditingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
