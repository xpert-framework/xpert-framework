package com.xpert.i18n;

import com.xpert.Configuration;
import com.xpert.utils.HumaniseCamelCase;
import com.xpert.utils.StringUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Logger;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author ayslan
 */
public class I18N {

    private static final Logger logger = Logger.getLogger(I18N.class.getName());

    public static String get(String key) {
        if (Configuration.getBundleName() == null) {
            return key;
        }
        return ResourceBundleUtils.get(key, Configuration.getBundleName(), Thread.currentThread().getContextClassLoader(), (Object[]) null);
    }

    public static String get(String key, Object... parameters) {
        if (Configuration.getBundleName() == null) {
            return key;
        }
        return ResourceBundleUtils.get(key, Configuration.getBundleName(), Thread.currentThread().getContextClassLoader(), parameters);
    }

    public static String getDatePattern() {
        return ((SimpleDateFormat) DateFormat.getDateInstance(SimpleDateFormat.SHORT, getLocale())).toPattern();
    }

    public static String getAttributeName(String classSimpleName, String fieldName) {
        String property = StringUtils.getLowerFirstLetter(classSimpleName) + "." + fieldName;
        return I18N.get(property);
    }

    /**
     *
     * @param clazz
     * @param fieldName
     *
     * @return the attribute name from configured resourcebundle the message
     * for: simple name (FirstLetter lowercase) + "." + property. Example: Class
     * Person and attribute name - person.name
     */
    public static String getAttributeName(Class clazz, String fieldName) {
        String property = StringUtils.getLowerFirstLetter(clazz.getSimpleName()) + "." + fieldName;
        String value = I18N.get(property);
        //try to find in superclass
        if ((value == null || value.isEmpty() || value.equals(property)) && clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return getAttributeName(clazz.getSuperclass(), fieldName);
        }
        if (value != null && value.equals(property)) {
            return new HumaniseCamelCase().humanise(fieldName);
        }
        return value;
    }

    public static Locale getLocale() {
        //for JSF Context
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            if (facesContext.getViewRoot() != null) {
                return facesContext.getViewRoot().getLocale();
            }
            if (facesContext.getApplication() != null && facesContext.getApplication().getDefaultLocale() != null) {
                return facesContext.getApplication().getDefaultLocale();
            }
        }
        return ResourceBundleUtils.PT_BR;
    }
}
