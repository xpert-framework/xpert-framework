package com.xpert.persistence.query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.TemporalType;

/**
 *
 * @author ayslan
 */
public class Restrictions extends ArrayList<Restriction> {

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, RestrictionType restrictionType) {
        this.add(new Restriction(property, restrictionType));
        return this;
    }

    /**
     * Add a RestrictionType.EQUALS
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, Object value) {
        this.add(new Restriction(property, value));
        return this;
    }

    /**
     * Add a Date restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, RestrictionType restrictionType, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, restrictionType, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, RestrictionType restrictionType, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, restrictionType, value, temporalType));
        return this;
    }

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, RestrictionType restrictionType, Object value) {
        this.add(new Restriction(property, restrictionType, value));
        return this;
    }

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param likeType
     * @return Current Restrictions with added restriction
     */
    public Restrictions add(String property, RestrictionType restrictionType, Object value, LikeType likeType) {
        this.add(new Restriction(property, restrictionType, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @return Current Restrictions with added restriction
     */
    public Restrictions addQueryString(String property) {
        this.add(new Restriction(property, RestrictionType.QUERY_STRING));
        return this;
    }
    
    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @param parameters
     * @return Current Restrictions with added restriction
     */
    public Restrictions addQueryString(String property, List<QueryParameter> parameters) {
        Restriction restriction =new Restriction(property, RestrictionType.QUERY_STRING);
        restriction.setParameters(parameters);
        this.add(restriction);
        return this;
    }
    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @param parameter
     * @return Current Restrictions with added restriction
     */
    public Restrictions addQueryString(String property, QueryParameter parameter) {
        Restriction restriction =new Restriction(property, RestrictionType.QUERY_STRING);
        List<QueryParameter> parameters = new ArrayList<QueryParameter>();
        parameters.add(parameter);
        restriction.setParameters(parameters);
        this.add(restriction);
        return this;
    }
    
    

    /**
     * Add a RestrictionType.NULL (property 'is null')
     *
     * @param property
     * @return Current Restrictions with added restriction
     */
    public Restrictions isNull(String property) {
        this.add(new Restriction(property, RestrictionType.NULL));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_NULL (property 'is not null')
     *
     * @param property
     * @return Current Restrictions with added restriction
     */
    public Restrictions isNotNull(String property) {
        this.add(new Restriction(property, RestrictionType.NOT_NULL));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions like(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LIKE, value));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @return Current Restrictions with added restriction
     */
    public Restrictions like(String property, Object value, LikeType likeType) {
        this.add(new Restriction(property, RestrictionType.LIKE, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions notLike(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @return Current Restrictions with added restriction
     */
    public Restrictions notLike(String property, Object value, LikeType likeType) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterEqualsThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterEqualsThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions greaterEqualsThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions lessThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions lessThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public Restrictions lessThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions lessEqualsThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions lessEqualsThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions lessEqualsThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.IN (property 'in' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions in(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.IN, value));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_IN (property 'not in' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions notIn(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_IN, value));
        return this;
    }

    /**
     * Add a RestrictionType.EQUALS (property '=' value)
     *
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions equals(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.EQUALS (property '=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions equals(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.EQUALS (property '=' value)
     * 
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions equals(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_EQUALS (property '!=' value)
     * 
     * @param property
     * @param value
     * @return Current Restrictions with added restriction
     */
    public Restrictions notEquals(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.NOT_EQUALS (property '!=' value)
     * 
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions notEquals(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.NOT_EQUALS (property '!=' value)
     * 
     * @param property
     * @param value
     * @param temporalType
     * @return Current Restrictions with added restriction
     */
    public Restrictions notEquals(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.OR
     * 
     * @return Current Restrictions with added restriction
     */
    public Restrictions or() {
        this.add(new Restriction(RestrictionType.OR));
        return this;
    }

    /**
     * Add a RestrictionType.START_GROUP
     * 
     * @return Current Restrictions with added restriction
     */
    public Restrictions startGroup() {
        this.add(new Restriction(RestrictionType.START_GROUP));
        return this;
    }

    /**
     * Add a RestrictionType.END_GROUP
     * 
     * @return Current Restrictions with added restriction
     */
    public Restrictions endGroup() {
        this.add(new Restriction(RestrictionType.END_GROUP));
        return this;
    }

    /**
     * @return current query String from added restrictions
     */
    public String getQueryString() {
        return QueryBuilder.getQueryStringFromRestrictions(this);
    }

}
