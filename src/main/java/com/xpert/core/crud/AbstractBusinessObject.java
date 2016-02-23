package com.xpert.core.crud;

import com.xpert.core.exception.BusinessException;
import com.xpert.core.exception.UniqueFieldException;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.exception.DeleteException;
import com.xpert.core.validation.UniqueField;
import com.xpert.core.validation.UniqueFieldsValidation;
import com.xpert.persistence.utils.EntityUtils;
import java.util.List;

/**
 * Generic Business Object to create CRUD
 *
 * @author ayslan
 * @param <T>
 */
public abstract class AbstractBusinessObject<T> {

    /**
     * @return The BusinessObject BaseDAO
     */
    public abstract BaseDAO getDAO();

    /**
     * Define the UniqueFields to be used in method "validateUniqueFields"
     *
     * @return
     */
    public abstract List<UniqueField> getUniqueFields();

    /**
     * Determine if entity will be audited on method "save()"
     *
     * @return
     */
    public abstract boolean isAudit();

    /**
     * Your own logic to validate the object
     *
     * @param object
     * @throws BusinessException
     */
    public abstract void validate(T object) throws BusinessException;

    /**
     * Validate unique field based on "getUniqueFields()"
     *
     * @param object
     * @throws UniqueFieldException
     */
    public void validateUniqueFields(T object) throws UniqueFieldException {
        List<UniqueField> uniqueFields = getUniqueFields();
        if (uniqueFields != null && !uniqueFields.isEmpty()) {
            UniqueFieldsValidation.validateUniqueFields(uniqueFields, object, getDAO());
        }
    }

    /**
     * Saves entity:
     * <ol>
     * <li>calls "validate(object)"</li>
     * <li>calls "validateUniqueFields(object)"</li>
     * <li>check if any excpetion occurred</li>
     * <li>if entity id is null then calls "save", if not, calls "merge"</li>
     * </ol>
     *
     * @param object
     * @throws BusinessException
     */
    public void save(T object) throws BusinessException {
        validate(object);
        validateUniqueFields(object);
        if (!EntityUtils.isPersisted(object)) {
            getDAO().save(object, isAudit());
        } else {
            getDAO().merge(object, isAudit());
        }
    }

    /**
     * Calls "baseDAO.delete()" to delete entity
     *
     * @param id entity id
     * @throws DeleteException
     */
    public void delete(Object id) throws DeleteException {
        getDAO().delete(id, isAudit());
    }

    /**
     * Calls "baseDAO.remove()" to delete entity
     *
     * @param id Entity id
     * @throws DeleteException
     */
    public void remove(Number id) throws DeleteException {
        Object object = getDAO().find(id);
        getDAO().remove(object, isAudit());
    }

    /**
     * Calls "baseDAO.remove()" to delete entity
     *
     * @param object Object to delete
     * @throws DeleteException
     */
    public void remove(T object) throws DeleteException {
        getDAO().remove(object, isAudit());
    }
}
