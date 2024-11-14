package com.xpert.faces.function;

import com.xpert.core.conversion.NumberUtils;
import com.xpert.faces.primefaces.LazyDataModelImpl;
import java.util.Collection;
import jakarta.faces.FacesException;
import jakarta.faces.model.DataModel;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class NumberFunctions implements Serializable {

    private static final long serialVersionUID = -2511327475403458739L;

    /**
     * If the object is a instanceof DataModel (example: standart DataModel,
     * primefaces LazyDataModel, xpert-framework LazyDataModelImpl ), return
     * getWrappedData()
     *
     * @param objects
     * @return
     */
    public static Collection getCollection(Object objects) {
        Collection collection = null;
        if (objects != null) {
            if (objects instanceof DataModel) {
                collection = (Collection) ((DataModel) objects).getWrappedData();
            } else if (objects instanceof Collection) {
                collection = (Collection) objects;
            } else {
                throw new FacesException("Type " + objects.getClass().getName() + " not supported in sum");
            }
        }
        return collection;
    }

    /**
     * If the object is instance of LazyDataModelImpl, then shoudld call avg
     * using query.
     *
     * @param objects
     * @param field
     * @return
     */
    public static Object max(Object objects, String field) {
        if (objects instanceof LazyDataModelImpl) {
            return ((LazyDataModelImpl) objects).max(field);
        }
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.max(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    /**
     * If the object is instance of LazyDataModelImpl, then shoudld call avg
     * using query.
     *
     * @param objects
     * @param field
     * @return
     */
    public static Object min(Object objects, String field) {
        if (objects instanceof LazyDataModelImpl) {
            return ((LazyDataModelImpl) objects).min(field);
        }
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.min(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    /**
     * If the object is instance of LazyDataModelImpl, then shoudld call avg
     * using query.
     *
     * @param objects
     * @param field
     * @return
     */
    public static Object avg(Object objects, String field) {
        if (objects instanceof LazyDataModelImpl) {
            return ((LazyDataModelImpl) objects).avg(field);
        }
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.avg(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    /**
     * If the object is instance of LazyDataModelImpl, then shoudld call sum
     * using query.
     *
     * @param objects
     * @param field
     * @return
     */
    public static Object sum(Object objects, String field) {
        if (objects instanceof LazyDataModelImpl) {
            return ((LazyDataModelImpl) objects).sum(field);
        }
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.sum(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    /**
     * Sum field with return of type Integer
     *
     * @param objects
     * @param field
     * @return
     */
    public static Integer sumInteger(Object objects, String field) {
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.sumInteger(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    /**
     * Sum field with return of type Long
     *
     * @param objects
     * @param field
     * @return
     */
    public static Long sumLong(Object objects, String field) {
        Collection collection = getCollection(objects);
        try {
            return NumberUtils.sumLong(collection, field);
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

}
