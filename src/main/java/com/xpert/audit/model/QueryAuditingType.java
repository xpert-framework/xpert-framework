package com.xpert.audit.model;

/**
 * Possible type to audit events
 *
 * @author ayslan
 */
public enum QueryAuditingType {

    FIND_BY_ID("find_by_id"),
    QUERY("query"),
    NATIVE_QUERY("native_query"),
    OTHER("other");

    private String description;

    QueryAuditingType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
