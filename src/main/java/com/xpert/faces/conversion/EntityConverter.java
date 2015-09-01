package com.xpert.faces.conversion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import org.hibernate.proxy.HibernateProxy;

/**
 * Generic converter to Objects. This converter puts object in faces ViewMap.
 * Classes with fields annotated with @Id (JPA) or @ConverterId
 * (xpert-framework) can be converted. It's important to override "equals" from
 * object and compare by id.
 *
 * @author ayslan
 */
public class EntityConverter implements Converter {

    private static final Logger logger = Logger.getLogger(EntityConverter.class.getName());
    private static final String ID_PREFIX = "entityConverter_";
    private static final Map<Class, Field> FIELDS_CACHE = new HashMap<Class, Field>();
    private static final Map<Class, Method> METHOD_CACHE = new HashMap<Class, Method>();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        if (value != null) {
            return getFromViewMap(context, component, value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component,
            Object object) {
        if (object != null && !"".equals(object)) {
            String id;

            id = this.getId(object);
            if (id == null) {
                id = "";
            }
            id = id.trim();
            putInViewMap(id, context, component, object);
            return id;
        }
        return null;
    }

    public void putInViewMap(String id, FacesContext context, UIComponent component, Object object) {
        if (object != null) {
            Map objectsFromClass = (Map) context.getViewRoot().getViewMap().get(ID_PREFIX + component.getId());
            if (objectsFromClass == null) {
                objectsFromClass = new HashMap();
                context.getViewRoot().getViewMap().put(ID_PREFIX + component.getId(), objectsFromClass);
            }
            objectsFromClass.put(id, object);
        }
    }

    public Object getFromViewMap(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.trim().isEmpty()) {
            Map objectsFromClass = (Map) context.getViewRoot().getViewMap().get(ID_PREFIX + component.getId());
            if (objectsFromClass != null) {
                return objectsFromClass.get(value);
            }
        }
        return null;
    }

    /**
     * Get object ID
     *
     * @param object
     * @return String
     */
    public String getId(Object object) {
        try {
            if (object instanceof HibernateProxy) {
                return ((HibernateProxy) object).getHibernateLazyInitializer().getIdentifier().toString();
            }
            Object id = getAnnotadedWithId(object);
            if (id != null) {
                return id.toString();
            } else {
                return "";
            }
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return null;
        }
    }

    public Object getAnnotadedWithId(Object object) {
        return getAnnotadedWithId(object, object.getClass());
    }

    public Object getAnnotadedWithId(Object object, Class clazz) {
        try {

            //try get field from cache
            Field fieldFromCache = FIELDS_CACHE.get(clazz);
            if (fieldFromCache != null) {
                return fieldFromCache.get(object);
            }

            //try get method from cache
            Method methodFromCache = METHOD_CACHE.get(clazz);
            if (methodFromCache != null) {
                return methodFromCache.invoke(object);
            }

            Field[] fields = clazz.getDeclaredFields();
            Method[] methods = clazz.getDeclaredMethods();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)  || field.isAnnotationPresent(ConverterId.class)) {
                    field.setAccessible(true);
                    FIELDS_CACHE.put(clazz, field);
                    return field.get(object);
                }
            }
            for (Method method : methods) {
                if (method.isAnnotationPresent(Id.class) || method.isAnnotationPresent(EmbeddedId.class) || method.isAnnotationPresent(ConverterId.class)) {
                    METHOD_CACHE.put(clazz, method);
                    return method.invoke(object);
                }
            }
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                return getAnnotadedWithId(object, clazz.getSuperclass());
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
