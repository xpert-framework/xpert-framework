package com.xpert.persistence.query;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.TemporalType;

/**
 *
 * @author Ayslan
 */
public class Restriction {

    private String property;
    private Object value;
    private RestrictionType restrictionType;
    private LikeType likeType;
    private TemporalType temporalType;
    private List<QueryParameter> parameters;
    private boolean ilike = true;

    public Restriction() {
    }

    /**
     *
     * @param restrictionType
     */
    public Restriction(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    /**
     *
     * @param property
     * @param restrictionType
     */
    public Restriction(String property, RestrictionType restrictionType) {
        this.property = property;
        this.restrictionType = restrictionType;
    }

    /**
     *
     * @param property
     * @param value
     */
    public Restriction(String property, Object value) {
        this.property = property;
        this.value = value;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     */
    public Restriction(String property, RestrictionType restrictionType, Date value, TemporalType temporalType) {
        this.property = property;
        this.value = value;
        this.temporalType = temporalType;
        this.restrictionType = restrictionType;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     */
    public Restriction(String property, RestrictionType restrictionType, Calendar value, TemporalType temporalType) {
        this.property = property;
        this.value = value;
        this.temporalType = temporalType;
        this.restrictionType = restrictionType;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     */
    public Restriction(String property, RestrictionType restrictionType, Object value) {
        this.property = property;
        this.value = value;
        this.restrictionType = restrictionType;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param ilike
     */
    public Restriction(String property, RestrictionType restrictionType, Object value, boolean ilike) {
        this.property = property;
        this.value = value;
        this.restrictionType = restrictionType;
        this.ilike = ilike;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param likeType
     */
    public Restriction(String property, RestrictionType restrictionType, Object value, LikeType likeType) {
        this.property = property;
        this.value = value;
        this.restrictionType = restrictionType;
        this.likeType = likeType;
    }

    /**
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param likeType
     * @param ilike
     */
    public Restriction(String property, RestrictionType restrictionType, Object value, LikeType likeType, boolean ilike) {
        this.property = property;
        this.value = value;
        this.restrictionType = restrictionType;
        this.likeType = likeType;
        this.ilike = ilike;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public LikeType getLikeType() {
        return likeType;
    }

    public void setLikeType(LikeType likeType) {
        this.likeType = likeType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Indicates if LIKE type is not case sensitive
     *
     * @return
     */
    public boolean isIlike() {
        return ilike;
    }

    /**
     * Set if LIKE type is not case sensitive
     *
     * @param ilike
     */
    public void setIlike(boolean ilike) {
        this.ilike = ilike;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Restriction other = (Restriction) obj;
        if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.restrictionType != other.restrictionType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.property != null ? this.property.hashCode() : 0);
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 53 * hash + (this.restrictionType != null ? this.restrictionType.hashCode() : 0);
        return hash;
    }

    /**
     * Creates a Restrition of type NULL
     *
     * @param property
     * @return
     */
    public static Restriction isNull(String property) {
        return new Restriction(property, RestrictionType.NULL);

    }

    /**
     * Creates a Restrition of type NOT_NULL
     *
     * @param property
     * @return
     */
    public static Restriction isNotNull(String property) {
        return new Restriction(property, RestrictionType.NOT_NULL);

    }

    /**
     * Creates a Restrition of type LIKE
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction like(String property, Object value) {
        return new Restriction(property, RestrictionType.LIKE, value);

    }

    /**
     * Creates a Restrition of type LIKE
     *
     * @param property
     * @param value
     * @param likeType
     * @return
     */
    public static Restriction like(String property, Object value, LikeType likeType) {
        return new Restriction(property, RestrictionType.LIKE, value, likeType);

    }

    /**
     * Creates a Restrition of type LIKE
     *
     * @param property
     * @param value
     * @param ilike
     * @return
     */
    public static Restriction like(String property, Object value, boolean ilike) {
        return new Restriction(property, RestrictionType.LIKE, value, ilike);

    }

    /**
     * Creates a Restrition of type LIKE
     *
     * @param property
     * @param value
     * @param likeType
     * @param ilike
     * @return
     */
    public static Restriction like(String property, Object value, LikeType likeType, boolean ilike) {
        return new Restriction(property, RestrictionType.LIKE, value, likeType, ilike);
    }

    /**
     * Creates a Restrition of type NOT_LIKE
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction notLike(String property, Object value) {
        return new Restriction(property, RestrictionType.NOT_LIKE, value);
    }

    /**
     * Creates a Restrition of type NOT_LIKE
     *
     * @param property
     * @param value
     * @param likeType
     * @return
     */
    public static Restriction notLike(String property, Object value, LikeType likeType) {
        return new Restriction(property, RestrictionType.NOT_LIKE, value, likeType);

    }

    /**
     * Creates a Restrition of type NOT_LIKE
     *
     * @param property
     * @param value
     * @param ilike
     * @return
     */
    public static Restriction notLike(String property, Object value, boolean ilike) {
        return new Restriction(property, RestrictionType.NOT_LIKE, value, ilike);
    }

    /**
     * Creates a Restrition of type NOT_LIKE
     *
     * @param property
     * @param value
     * @param likeType
     * @param ilike
     * @return
     */
    public static Restriction notLike(String property, Object value, LikeType likeType, boolean ilike) {
        return new Restriction(property, RestrictionType.NOT_LIKE, value, likeType, ilike);

    }

    /**
     * Creates a Restrition of type GREATER_THAN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction greaterThan(String property, Object value) {
        return new Restriction(property, RestrictionType.GREATER_THAN, value);

    }

    /**
     * Creates a Restrition of type GREATER_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction greaterThan(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type GREATER_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction greaterThan(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type GREATER_EQUALS_THHAN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction greaterEqualsThan(String property, Object value) {
        return new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value);

    }

    /**
     * Creates a Restrition of type GREATER_EQUAS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction greaterEqualsThan(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type GREATER_EQUALS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction greaterEqualsThan(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type LESS_THAN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction lessThan(String property, Object value) {
        return new Restriction(property, RestrictionType.LESS_THAN, value);

    }

    /**
     * Creates a Restrition of type LESS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction lessThan(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.LESS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type LESS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction lessThan(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.LESS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type LESS_EQUAS_THAN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction lessEqualsThan(String property, Object value) {
        return new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value);

    }

    /**
     * Creates a Restrition of type LESS_EQUALS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction lessEqualsThan(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type LESS_EQUALS_THAN
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction lessEqualsThan(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType);

    }

    /**
     * Creates a Restrition of type IN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction in(String property, Object value) {
        return new Restriction(property, RestrictionType.IN, value);

    }

    /**
     * Creates a Restrition of type NOT_IN
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction notIn(String property, Object value) {
        return new Restriction(property, RestrictionType.NOT_IN, value);

    }

    /**
     * Creates a Restrition of type EQUALS
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction equals(String property, Object value) {
        return new Restriction(property, RestrictionType.EQUALS, value);

    }

    /**
     * Creates a Restrition of type EQUALS
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction equals(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.EQUALS, value, temporalType);

    }

    /**
     * Creates a Restrition of type EQUALS
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction equals(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.EQUALS, value, temporalType);

    }

    /**
     * Creates a Restrition of type NOT_EQUALS
     *
     * @param property
     * @param value
     * @return
     */
    public static Restriction notEquals(String property, Object value) {
        return new Restriction(property, RestrictionType.NOT_EQUALS, value);

    }

    /**
     * Creates a Restrition of type NOT_EQUALS
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction notEquals(String property, Date value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType);

    }

    /**
     * Creates a Restrition of type NOT_EQUALS
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public static Restriction notEquals(String property, Calendar value, TemporalType temporalType) {
        return new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType);

    }

    public List<QueryParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<QueryParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (property != null) {
            builder.append("(Property: ").append(property).append(") ");
        }
        if (restrictionType != null) {
            builder.append("(RestrictionType: ").append(restrictionType.name()).append(") ");
        }

        if (value != null) {
            builder.append("(Value: ").append(value).append(") ");
            builder.append("(Type: ").append(value.getClass().getName()).append(") ");
        }

        if (restrictionType != null && restrictionType.equals(RestrictionType.LIKE)) {
            if (likeType != null) {
                builder.append("(LikeType: ").append(likeType.name()).append(") ");
            }
            builder.append("(Ilike: ").append(ilike).append(") ");
        }
        if (temporalType != null) {
            builder.append("(TemporalType: ").append(temporalType.name()).append(") ");
        }
        builder.append("}");
        return builder.toString();
    }
}
