package com.xpert.persistence.query;

import javax.persistence.TemporalType;

/**
 *
 * @author Ayslan
 */
public class QueryParameter {

    private Integer position;
    private String property;
    private Object value;
    private TemporalType temporalType;

    public QueryParameter(int position, Object value, TemporalType temporalType) {
        this.position = position;
        this.value = value;
        this.temporalType = temporalType;
    }

    public QueryParameter(int position, Object value) {
        this.position = position;
        this.value = value;
    }

    public QueryParameter(String property, Object value) {
        this.property = property;
        this.value = value;
    }

    public QueryParameter(String property, Object value, TemporalType temporalType) {
        this.property = property;
        this.value = value;
        this.temporalType = temporalType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
    

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    @Override
    public String toString() {
        return "QueryParameter{" + "position=" + position + ", value=" + value + ", temporalType=" + temporalType + '}';
    }
    
    
    
}
