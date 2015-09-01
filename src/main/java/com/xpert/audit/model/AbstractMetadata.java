package com.xpert.audit.model;

import com.xpert.i18n.I18N;
import javax.persistence.MappedSuperclass;

/**
 * Abstract class to represent the fields of an audit event. Each metadata is a field from the entity that has a "old value" and "new value".
 * 
 * @author Ayslan
 */
@MappedSuperclass
public abstract class AbstractMetadata {

    private String field;
    private String oldValue;
    private String newValue;
    private String entity;
    private Long newIdentifier;
    private Long oldIdentifier;

    public abstract Object getId();

    public abstract AbstractAuditing getAuditing();

    public abstract void setAuditing(AbstractAuditing auditing);
    
    /**
     * defines max size to fields "oldValue" and "newValue"
     * 
     * @return 
     */
    public Integer getMaxSizeValues(){
        return null;
    }

    /**
     * @param clazz auditing class
     *
     * @return the attribute name from configured resourcebundle the message
     * for: simple name (FirstLetter lowercase) + "." + property. Example: Class
     * Person and attribute name - person.name
     */
    public String getFieldName(Class clazz) {
        if (field != null) {
            return I18N.getAttributeName(clazz, field);
        }
        return field;
    }

    public String getFieldName() {
        AbstractAuditing auditing = getAuditing();
        if (auditing != null && auditing.getAuditClass() != null) {
            return getFieldName(auditing.getAuditClass());
        }
        return field;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Long getNewIdentifier() {
        return newIdentifier;
    }

    public void setNewIdentifier(Long newIdentifier) {
        this.newIdentifier = newIdentifier;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Long getOldIdentifier() {
        return oldIdentifier;
    }

    public void setOldIdentifier(Long oldIdentifier) {
        this.oldIdentifier = oldIdentifier;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
