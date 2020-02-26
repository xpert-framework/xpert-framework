package com.xpert.persistence.utils;

import com.xpert.utils.StringUtils;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.hibernate.proxy.HibernateProxy;

/**
 * Utility class to manipulae JPA entities
 *
 * @author ayslan
 */
public class EntityUtils {

    private static final Logger logger = Logger.getLogger(EntityUtils.class.getName());
    private static final Map<Class, String> ID_NAME_MAP = new HashMap<>();
    private static final Map<Class, AccessibleObject> ID_ACCESSIBLE_MAP = new HashMap<>();
    private static final Map<Class, Class> ID_TYPE_MAP = new HashMap<>();

    /**
     * Convert a string to a mapped id type.
     *
     * Example:
     * <pre>
     * <code>
     * &#064;Id
     * private Integer id;
     * </code>
     * </pre> With string "10" returns a Integer value 10
     *
     * @param id
     * @param entityClass
     * @return
     */
    public static Object getIdFromString(String id, Class entityClass) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        try {
            Class idType = EntityUtils.getIdType(entityClass);
            if (idType == null) {
                throw new IllegalArgumentException("Id not found in entity " + entityClass.getName());
            } else if (idType.equals(Long.class) || idType.equals(long.class)) {
                return Long.parseLong(StringUtils.getOnlyIntegerNumbers(id));
            } else if (idType.equals(Integer.class) || idType.equals(int.class)) {
                return Integer.parseInt(StringUtils.getOnlyIntegerNumbers(id));
            } else if (idType.equals(BigInteger.class)) {
                return new BigInteger(StringUtils.getOnlyIntegerNumbers(id));
            } else if (idType.equals(Short.class) || idType.equals(short.class)) {
                return Short.parseShort(StringUtils.getOnlyIntegerNumbers(id));
            } else if (idType.equals(BigDecimal.class)) {
                return new BigDecimal(StringUtils.getOnlyIntegerNumbers(id));
            } else if (idType.equals(String.class)) {
                return id;
            } else if (idType.equals(UUID.class)) {
                return UUID.fromString(id);
            } else {
                throw new IllegalArgumentException("Type " + idType.getName() + " from entity " + entityClass.getName() + " cannot be converted");
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns a entity table name. Try to get from @Table annotation, if not
     * exsits try to get @Entity, if not exists return class.getSimpleName()
     *
     * @param entity
     * @param nameWithSchema Indicate if name comes with schema. Example:
     * yourShema.tableName
     * @return
     */
    public static String getEntityTableName(Class entity, boolean nameWithSchema) {

        String name = null;
        String schema = null;

        Table table = (Table) entity.getAnnotation(Table.class);
        if (table != null && table.name() != null && !table.name().isEmpty()) {
            name = table.name();
            schema = table.schema();
        } else {
            Entity entityAnnotation = (Entity) entity.getAnnotation(Entity.class);
            if (entityAnnotation != null && entityAnnotation.name() != null && !entityAnnotation.name().isEmpty()) {
                name = entityAnnotation.name();
            } else {
                name = entity.getSimpleName();
            }
        }

        if (nameWithSchema == true && schema != null) {
            name = schema + "." + name;
        }

        return name;
    }

    /**
     * Return mapped entities in EntityManager. Classes are get from
     * ClassMetadata in SessionFactory
     *
     * @param entityManager
     * @return
     */
    public static List<Class> getMappedEntities(EntityManager entityManager) {
        EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        Metamodel metamodel = entityManagerFactory.getMetamodel();
        List<Class> classes = new ArrayList<>();
        for (EntityType entityType : metamodel.getEntities()) {
            classes.add(entityType.getBindableJavaType());
        }
        return classes;
    }

    /**
     * Return true is @GeneratedValue is present in id
     *
     * @param clazz
     * @return
     */
    public static boolean hasAutoGeneratedId(Class clazz) {
        AccessibleObject accessibleObject = getIdAccessibleObject(clazz);
        if (accessibleObject != null && accessibleObject.isAnnotationPresent(GeneratedValue.class)) {
            return true;
        }
        return false;
    }

    /**
     * Return true if @EmbeddedId is present in id
     *
     * @param clazz
     * @return
     */
    public static boolean hasEmbeddedId(Class clazz) {
        AccessibleObject accessibleObject = getIdAccessibleObject(clazz);
        if (accessibleObject != null && accessibleObject.isAnnotationPresent(EmbeddedId.class)) {
            return true;
        }
        return false;
    }

    /**
     * Method to return entity id (field/method with annotation @Id or
     *
     * @EmbeddedId)
     *
     * @param object
     * @return
     */
    public static Object getId(Object object) {
        if (object == null) {
            return null;
        }
        return getId(object, object.getClass());
    }

    /**
     * Returns true if entity has a not null @Id/ @EmbeddedId
     *
     * @param object
     * @return
     */
    public static boolean isPersisted(Object object) {
        return getId(object) != null;
    }

    /**
     * Method to return entity id (field/method with annotation @Id or
     *
     * @EmbeddedId)
     *
     * @param object
     * @param clazz current class
     * @return
     */
    public static Object getId(Object object, Class clazz) {

        try {
            if (object == null) {
                return null;
            }

            if (object instanceof HibernateProxy) {
                return ((HibernateProxy) object).getHibernateLazyInitializer().getIdentifier();
            }

            AccessibleObject accessibleObject = getIdAccessibleObject(clazz);
            if (accessibleObject instanceof Field) {
                Field field = (Field) accessibleObject;
                field.setAccessible(true);
                return field.get(object);
            }

            if (accessibleObject instanceof Method) {
                Method method = (Method) accessibleObject;
                return method.invoke(object);
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Return a class from object id object is null then return null, if object
     * instance of HibernateProxy then return
     * getHibernateLazyInitializer().getPersistentClass()
     *
     * @param object
     * @return
     */
    public static Class getPersistentClass(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof HibernateProxy) {
            return ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass();
        }
        return object.getClass();
    }

    /**
     * Return the type of @Id/@EmbeddedId from entity. Example: Integer.class,
     * Long.class
     *
     * @param clazz
     * @return
     */
    public static Class getIdType(Class clazz) {
        Class type = ID_TYPE_MAP.get(clazz);
        if (type != null) {
            return type;
        }
        AccessibleObject accessibleObject = getIdAccessibleObject(clazz);
        if (accessibleObject instanceof Field) {
            type = ((Field) accessibleObject).getType();
            ID_TYPE_MAP.put(clazz, type);
            return type;
        }

        if (accessibleObject instanceof Method) {
            type = ((Method) accessibleObject).getReturnType();
            ID_TYPE_MAP.put(clazz, type);
            return type;
        }
        return null;
    }

    /**
     * Return the name of @Id/@EmbeddedId field/method from entity
     *
     * @param object
     * @return
     */
    public static String getIdFieldName(Object object) {
        return getIdFieldName(object.getClass());
    }

    /**
     * Return the name of @Id/@EmbeddedId field/method from entity
     *
     * @param clazz
     * @return
     */
    public static String getIdFieldName(Class clazz) {

        String nameFromMap = ID_NAME_MAP.get(clazz);
        if (nameFromMap != null) {
            return nameFromMap;
        }

        AccessibleObject accessibleObject = getIdAccessibleObject(clazz);

        if (accessibleObject instanceof Field) {
            String name = ((Field) accessibleObject).getName();
            ID_NAME_MAP.put(clazz, name);
            return name;
        }

        if (accessibleObject instanceof Method) {
            String name = ((Method) accessibleObject).getName();
            String withoutGet = name.substring(3, name.length());
            withoutGet = getLowerFirstLetter(withoutGet);
            ID_NAME_MAP.put(clazz, withoutGet);
            return withoutGet;
        }

        return null;
    }

    /**
     * Return field/method with annotation @Id/@EmbeddedId from a entity class.
     * AccessibleObject is superclass of Field and Method.
     *
     * @param clazz
     * @return
     */
    public static AccessibleObject getIdAccessibleObject(Class clazz) {

        AccessibleObject accessibleObjectFromMap = ID_ACCESSIBLE_MAP.get(clazz);
        if (accessibleObjectFromMap != null) {
            return accessibleObjectFromMap;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
                ID_ACCESSIBLE_MAP.put(clazz, field);
                return field;
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Id.class) || method.isAnnotationPresent(EmbeddedId.class)) {
                ID_ACCESSIBLE_MAP.put(clazz, method);
                return method;
            }
        }
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return getIdAccessibleObject(clazz.getSuperclass());
        }
        return null;
    }

    public static String getLowerFirstLetter(String string) {
        if (string.length() == 1) {
            return string.toLowerCase();
        }
        if (string.length() > 1) {
            return string.substring(0, 1).toLowerCase() + "" + string.substring(1, string.length());
        }
        return "";
    }

    /**
     * Returns true if the class has the annotation @Entity
     *
     * @param clazz
     * @return
     */
    public static boolean isEntity(Class clazz) {
        if (clazz.isAnnotationPresent(Entity.class)) {
            return true;
        }
        if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return isEntity(clazz.getSuperclass());
        }
        return false;
    }
}
