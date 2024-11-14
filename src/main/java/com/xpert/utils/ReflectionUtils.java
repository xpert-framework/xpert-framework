package com.xpert.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to use reflections operations
 *
 * @author ayslan
 */
public class ReflectionUtils implements Serializable {

    private static final long serialVersionUID = -7860341095754473312L;
    
    private static final Logger logger = Logger.getLogger(ReflectionUtils.class.getName());
    /**
     * the possible prefixes for read method
     */
    private static final String[] getterPrefixes = {"get", "is"};

    /**
     * Gets the type of a property of an object.
     *
     * @param object The object that the property belongs to, cannot be null.
     * @param property The property to get type, can be nested. for example,
     * 'foo.bar.baz'.
     * @return The type of the property. If the property doesn't exists in the
     * object, returns null.
     */
    public static Class getPropertyType(Object object, String property) {
        if (object == null) {
            throw new IllegalArgumentException("Object cannot be null.");
        }
        return getPropertyType(object.getClass(), property);
    }

    /**
     * Gets the type of a property of a class.
     *
     * @param clazz The class that the property belongs to, cannot be null.
     * @param property The property to get type, can be nested. for example,
     * 'foo.bar.baz'.
     * @return The type of the property. If the property doesn't exists in the
     * clazz, returns null.
     */
    public static Class getPropertyType(Class clazz, String property) {
        if (clazz == null) {
            throw new IllegalArgumentException("Clazz cannot be null.");
        }

        if (property == null) {
            throw new IllegalArgumentException("Property cannot be null.");
        }

        int dotIndex = property.lastIndexOf('.');

        if (dotIndex == -1) {
            Method method = getReadMethod(clazz, property);
            return method == null ? null : method.getReturnType();
        }

        String deepestProperty = property.substring(dotIndex + 1);
        String parentProperty = property.substring(0, dotIndex);
        return getPropertyType(getPropertyType(clazz, parentProperty), deepestProperty);
    }

    /**
     * Gets the read method for a property in a class.
     *
     * for example:      <code>
     * class Foo {
     *      public String getBar() { return "bar"; }
     *      public Boolean isBaz() { return false; }
     * }
     *
     * BeanUtils.getReadMethod(Foo.class, "bar"); // return Foo#getBar()
     * BeanUtils.getReadMethod(Foo.class, "baz"); // return Foo#isBaz()
     * BeanUtils.getReadMethod(Foo.class, "baa"); // return null
     * </code>
     *
     * @param clazz The class to get read method.
     * @param property The property to get read method for, can NOT be nested.
     * @return The read method (getter) for the property, if there is no read
     * method for the property, returns null.
     */
    public static Method getReadMethod(Class clazz, String property) {
        // Capitalize the property 
        StringBuilder buf = new StringBuilder();
        buf.append(property.substring(0, 1).toUpperCase());
        if (property.length() > 1) {
            buf.append(property.substring(1));
        }

        Method method = null;
        for (String prefix : getterPrefixes) {
            String methodName = prefix + buf.toString();
            try {
                method = clazz.getMethod(methodName);

                // Once get method successfully, jump out the loop. 
                break;
            } catch (NoSuchMethodException e) {
                // do nothing
            } catch (SecurityException e) {
                // do nothing but logging
                logger.log(Level.WARNING, "Error occurs while getting read method ''{0}()'' in class ''{1}''.", new Object[]{methodName, clazz.getName()});
            }
        }

        return method;
    }

    /**
     * Return all Declared fields from class and superclass
     *
     * @param type
     * @return
     */
    public static List<Field> getDeclaredFields(Class type) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            fields.addAll(getDeclaredFields(type.getSuperclass()));
        }
        return fields;
    }

    /**
     * Return all Declared Methods from class and superclass
     *
     * @param type
     * @return
     */
    public static List<Method> getDeclaredMethods(Class type) {
        List<Method> methods = new ArrayList<>();
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));
        if (type.getSuperclass() != null && !type.getSuperclass().equals(Object.class)) {
            methods.addAll(getDeclaredMethods(type.getSuperclass()));
        }
        return methods;
    }

    /**
     * get a declared field from class or super classes
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getDeclaredField(Class clazz, String fieldName) {
        Field field = null;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                return getDeclaredField(clazz.getSuperclass(), fieldName);
            }
        }

        return field;
    }
    

    /**
     * Return method field name
     *
     * @param method
     * @return
     */
    public static String getMethodName(Method method) {
        if (method.getName().startsWith("is")) {
            return method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
        } else if (method.getName().startsWith("get")) {
            return method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
        }
        return null;
    }
}
