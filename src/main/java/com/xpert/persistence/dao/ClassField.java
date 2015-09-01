package com.xpert.persistence.dao;

/**
 *
 * @author ayslan
 */
public class ClassField {

    private String field;
    private Class clazz;

    public ClassField(Class clazz, String field) {
        this.clazz = clazz;
        this.field = field;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClassField other = (ClassField) obj;
        if ((this.field == null) ? (other.field != null) : !this.field.equals(other.field)) {
            return false;
        }
        if (this.clazz != other.clazz && (this.clazz == null || !this.clazz.equals(other.clazz))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.field != null ? this.field.hashCode() : 0);
        hash = 79 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
        return hash;
    }
}
