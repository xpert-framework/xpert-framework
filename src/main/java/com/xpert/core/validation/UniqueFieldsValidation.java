package com.xpert.core.validation;

import com.xpert.core.exception.UniqueFieldException;
import com.xpert.i18n.I18N;
import com.xpert.i18n.XpertResourceBundle;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import com.xpert.persistence.utils.EntityUtils;
import com.xpert.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author Ayslan
 */
public class UniqueFieldsValidation {

    public static void validateUniqueFields(UniqueField uniqueField, Object object, BaseDAO baseDAO) throws UniqueFieldException {
        if (uniqueField != null) {
            UniqueFields uniqueFields = new UniqueFields();
            uniqueFields.add(uniqueField);
            validateUniqueFields(uniqueFields, object, baseDAO);
        }
    }

    public static void validateUniqueFields(List<UniqueField> uniqueFields, Object object, BaseDAO baseDAO) throws UniqueFieldException {

        if (uniqueFields == null) {
            return;
        }

        UniqueFieldException exception = new UniqueFieldException();

        for (UniqueField uniqueField : uniqueFields) {
            List<Restriction> restrictions = new ArrayList<Restriction>();
            if (uniqueField.getRestrictions() != null && !uniqueField.getRestrictions().isEmpty()) {
                restrictions.addAll(uniqueField.getRestrictions());
            }
            for (String fieldName : uniqueField.getConstraints()) {
                try {
                    Object value = PropertyUtils.getProperty(object, fieldName);
                    if (value != null && !value.toString().isEmpty()) {
                        restrictions.add(new Restriction(fieldName, value));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (!restrictions.isEmpty()) {
                Object id = EntityUtils.getId(object);
                if (id != null) {
                    restrictions.add(new Restriction(EntityUtils.getIdFieldName(object), RestrictionType.NOT_EQUALS, id));
                }
                if (baseDAO.count(restrictions) > 0) {
                    if (uniqueField.getMessage() != null && !uniqueField.getMessage().isEmpty()) {
                        exception.add(uniqueField.getMessage());
                    } else {
                        exception.setI18n(false);
                        exception.add(getValidationMessage(uniqueField, object));
                    }
                }
            }
        }
        exception.check();
    }

    private static String getValidationMessage(UniqueField uniqueField, Object object) {
        String lowerClassName = StringUtils.getLowerFirstLetter(object.getClass().getSimpleName());
        StringBuilder properties = new StringBuilder();
        properties.append(I18N.get((getLowerClassName(object.getClass(), uniqueField.getConstraints()[0])) + "." + uniqueField.getConstraints()[0]));
        if (uniqueField.getConstraints().length > 1) {
            int it = 0;
            for (String fieldName : uniqueField.getConstraints()) {
                if (it > 0) {
                    if (it + 1 == uniqueField.getConstraints().length) {
                        properties.append(" ").append(XpertResourceBundle.get("and")).append(" ");
                    } else if (it > 0) {
                        properties.append(", ");
                    }
                    properties.append(I18N.get((getLowerClassName(object.getClass(), fieldName)) + "." + fieldName));
                }
                it++;
            }
        }
        return XpertResourceBundle.get("alreadyRegisteredWithField", I18N.get(lowerClassName), properties.toString());
    }

    private static String getLowerClassName(Class clazz, String fieldName) {
        String className = null;
        try {
            clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                className = getLowerClassName(clazz.getSuperclass(), fieldName);
            }
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
        if (className == null) {
            className = clazz.getSimpleName();
        }

        return StringUtils.getLowerFirstLetter(className);
    }
}
