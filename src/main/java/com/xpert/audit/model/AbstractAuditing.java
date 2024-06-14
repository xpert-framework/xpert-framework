package com.xpert.audit.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import java.util.Date;
import java.util.List;

/**
 * Abstract class to represent a audit event.
 * 
 * @author ayslan
 */
@MappedSuperclass
public abstract class AbstractAuditing {

    @Transient
    private Class auditClass;
    
    private String entity;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;
    
    private Long identifier;
    
    private String ip;
    
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private AuditingType auditingType;

    public abstract Object getId();

    public abstract List getMetadatas();
    
    public abstract String getUserName();
    
    public abstract void setMetadatas(List metadatas);
    
    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    
    
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    public AuditingType getAuditingType() {
        return auditingType;
    }

    public void setAuditingType(AuditingType auditingType) {
        this.auditingType = auditingType;
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
    
    
}
