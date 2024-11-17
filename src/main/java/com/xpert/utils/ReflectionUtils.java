package com.xpert.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    /**
     *
     * @param <T>
     * @param target
     * @param methodName
     * @param parameters
     * @return
     */
    public static <T> T invokeMethod(Object target, String methodName, Object... parameters) {
        try {
            Method method = getMethod(target, methodName, toClassArray(parameters));
            method.setAccessible(true);
            return (T) method.invoke(target, parameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error invoking method: {0}", methodName);
            throw new RuntimeException("Method invocation failed: " + methodName, e);
        }
    }

    /**
     *
     * @param <T>
     * @param target
     * @param fieldName
     * @return
     */
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = getField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     *
     * @param <T>
     * @param method
     * @param parameters
     * @return
     */
    public static <T> T invokeStaticMethod(Method method, Object... parameters) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(null, parameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.severe("Error invoking static method.");
            throw new RuntimeException("Static method invocation failed", e);
        }
    }

    /**
     *
     * @param target
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Object target, String methodName, Class... parameterTypes) {
        return getMethod(target.getClass(), methodName, parameterTypes);
    }

    /**
     *
     * @param targetClass
     * @param methodName
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class targetClass, String methodName, Class... parameterTypes) {
        try {
            return targetClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return targetClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignore) {
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getMethod(targetClass.getSuperclass(), methodName, parameterTypes);
            } else {
                logger.log(Level.SEVERE, "Method [{0}] not found in class [{1}]", new Object[]{methodName, targetClass.getName()});
                throw new RuntimeException("Method not found: " + methodName, e);
            }
        }
    }

    /**
     *
     * @param targetClass
     * @param fieldName
     * @return
     */
    public static Field getField(Class targetClass, String fieldName) {
        try {
            return targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                return targetClass.getField(fieldName);
            } catch (NoSuchFieldException ignore) {
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getField(targetClass.getSuperclass(), fieldName);
            } else {
                logger.log(Level.SEVERE, "Field [{0}] not found in class [{1}]", new Object[]{fieldName, targetClass.getName()});
                throw new RuntimeException("Field not found: " + fieldName, e);
            }
        }
    }

    /**
     *
     * @param parameters
     * @return
     */
    public static Class[] toClassArray(Object[] parameters) {
        return Arrays.stream(parameters)
                .map(Object::getClass)
                .toArray(Class[]::new);
    }

}
