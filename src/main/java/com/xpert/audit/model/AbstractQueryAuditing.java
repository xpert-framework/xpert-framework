package com.xpert.audit.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.io.Serializable;

import java.util.Date;

/**
 * Abstract class to represent a audit event in a Query.
 * 
 * @author ayslan
 */
@MappedSuperclass
public abstract class AbstractQueryAuditing implements Serializable {

    private static final long serialVersionUID = -8049049608813826454L;

    public static final int SQL_STRING_MAX_SIZE = 2000;

    @Transient
    private Class auditClass;

    private String entity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private Long timeMilliseconds;

    private Long identifier;

    private String ip;

    @Column(length = SQL_STRING_MAX_SIZE)
    private String sqlQuery;
    @Column(length = SQL_STRING_MAX_SIZE)
    private String sqlParameters;
    private Integer maxResults;
    private Integer firstResult;
    private Long rowsTotal;
    
    private boolean paginatedQuery;
    private boolean hasQueryParameter;
    private Integer parametersSize;
    
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private QueryAuditingType auditingType;
    

    public abstract Object getId();

    public abstract String getUserName();

    public void calculateTime() {
        if (this.startDate != null && this.endDate != null) {
            this.timeMilliseconds = this.endDate.getTime() - this.startDate.getTime();
        }
    }

    public QueryAuditingType getAuditingType() {
        return auditingType;
    }

    public void setAuditingType(QueryAuditingType auditingType) {
        this.auditingType = auditingType;
    }
    
    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getFirstResult() {
        return firstResult;
    }

    public void setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
    }

    public Long getRowsTotal() {
        return rowsTotal;
    }

    public void setRowsTotal(Long rowsTotal) {
        this.rowsTotal = rowsTotal;
    }

    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Class getAuditClass() {
        return auditClass;
    }

    public void setAuditClass(Class auditClass) {
        this.auditClass = auditClass;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getTimeMilliseconds() {
        return timeMilliseconds;
    }

    public void setTimeMilliseconds(Long timeMilliseconds) {
        this.timeMilliseconds = timeMilliseconds;
    }

    public String getSqlParameters() {
        return sqlParameters;
    }

    public void setSqlParameters(String sqlParameters) {
        this.sqlParameters = sqlParameters;
    }

    public boolean isPaginatedQuery() {
        return paginatedQuery;
    }

    public void setPaginatedQuery(boolean paginatedQuery) {
        this.paginatedQuery = paginatedQuery;
    }

    public boolean isHasQueryParameter() {
        return hasQueryParameter;
    }

    public void setHasQueryParameter(boolean hasQueryParameter) {
        this.hasQueryParameter = hasQueryParameter;
    }

    public Integer getParametersSize() {
        return parametersSize;
    }

    public void setParametersSize(Integer parametersSize) {
        this.parametersSize = parametersSize;
    }

}
