package com.xpert.core.validation;

import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.Restrictions;
import java.util.ArrayList;

/**
 *
 * @author ayslan
 */
public class UniqueFields extends ArrayList<UniqueField> {

    /**
     *
     * @param fields
     */
    public UniqueFields(String... fields) {
        add(new UniqueField(fields));
    }

    /**
     *
     * @param restrictions
     * @param fields
     */
    public UniqueFields(Restrictions restrictions, String... fields) {
        add(new UniqueField(restrictions, fields));
    }

    /**
     *
     * @param restriction
     * @param fields
     */
    public UniqueFields(Restriction restriction, String... fields) {
        add(new UniqueField(restriction, fields));
    }

    public UniqueFields add(Restriction restriction, String... fields) {
        add(new UniqueField(restriction, fields));
        return this;
    }

    public UniqueFields add(String... fields) {
        add(new UniqueField(fields));
        return this;
    }

    public UniqueFields add(UniqueField uniqueField, String message) {
        add(uniqueField.setMessage(message));
        return this;
    }

}
