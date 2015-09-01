package com.xpert.core.validation;

import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.Restrictions;

/**
 *
 * @author Ayslan
 */
public class UniqueField {

    private String message;
    private String[] constraints;
    private Restrictions restrictions = new Restrictions();

    /**
     *
     * @param constraints
     */
    public UniqueField(String... constraints) {
        this.constraints = constraints;
    }

    /**
     *
     * @param restrictions
     * @param constraints
     */
    public UniqueField(Restrictions restrictions, String... constraints) {
        this.constraints = constraints;
        this.restrictions = restrictions;
    }

    /**
     *
     * @param restriction
     * @param constraints
     */
    public UniqueField(Restriction restriction, String... constraints) {
        this.constraints = constraints;
        this.restrictions.add(restriction);
    }

    public UniqueField setMessage(String message) {
        this.message = message;
        return this;
    }

    public String[] getConstraints() {
        return constraints;
    }

    public void setConstraints(String[] constraints) {
        this.constraints = constraints;
    }

    public String getMessage() {
        return message;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

}
