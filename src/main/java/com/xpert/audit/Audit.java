package com.xpert.audit;

import com.xpert.audit.model.AbstractAuditing;
import com.xpert.audit.model.AbstractMetadata;
import com.xpert.audit.model.AuditingType;
import com.xpert.Configuration;
import com.xpert.faces.utils.FacesUtils;
import com.xpert.persistence.utils.EntityUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;

/**
 * This class audits a entity. The audit types are represented by enum
 * AuditingType. The values are compared by reflection. Classes, fields and
 * methdos annotated with @NotAudit must be ignored.
 *
 * @author ayslan
 */
public class Audit {

    private static final Logger logger = Logger.getLogger(Audit.class.getName());
    private static final String[] EXCLUDED_FIELDS = {"notifyAll", "notify", "getClass", "wait", "hashCode", "toString", "equals"};
    private static final Map<Class, String> MAPPED_NAME = new HashMap<Class, String>();
    private static final Map<Class, List<Method>> MAPPED_METHODS = new HashMap<Class, List<Method>>();
    private static final Map<Method, Boolean> MAPPED_ONE_TO_ONE_CASCADE_ALL = new HashMap<Method, Boolean>();
    private final EntityManager entityManager;
    private final EntityManager auditEntityManager;
    private final SimpleDateFormat AUDIT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Audit(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.auditEntityManager = entityManager;
    }

    public Audit(EntityManager entityManager, EntityManager auditEntityManager) {
        this.entityManager = entityManager;
        this.auditEntityManager = auditEntityManager;
    }

    /**
     * Get the object from database
     *
     * @param object
     * @return the object from database
     */
    public Object getPersisted(Object object) {
        Object id = EntityUtils.getId(object);
        if (id != null) {
            if (entityManager.contains(object)) {
                entityManager.detach(object);
            }
            return entityManager.find(EntityUtils.getPersistentClass(object), id);
        }
        return null;
    }

    public Object getPersistedById(Object id, Class clazz) {
        if (id != null) {
            return entityManager.find(clazz, id);
        }
        return null;
    }

    /**
     * Audit a "insert" event. Must be called after merge/persist the entity.
     *
     * Usage example:
     * <pre>
     * {@code
     * Audit audit = new Audit(entityManager);
     * object = entityManager.merge(object);
     * audit.insert(object);
     * }
     * </pre>
     *
     * @param object Object to be audited
     */
    public void insert(Object object) {
        if (!Audit.isAudit(object)) {
            return;
        }
        audit(object, null, AuditingType.INSERT);
    }

    /**
     * Audit a "update" event. Must be called before merge/persist the entity.
     *
     * Usage example:
     * <pre>
     * {@code
     * Audit audit = new Audit(entityManager);
     * audit.update(object);
     * object = entityManager.merge(object);
     * }
     * </pre>
     *
     * @param object Object to be audited
     */
    public void update(Object object) {
        if (!Audit.isAudit(object)) {
            return;
        }
        Object persisted = getPersisted(object);
        if (persisted != null) {
            audit(object, persisted, AuditingType.UPDATE);
            entityManager.detach(persisted);
        } else {
            logger.log(Level.SEVERE, "Entity {0} passed to update in Xpert Audit, is a transient instance",
                    new Object[]{EntityUtils.getPersistentClass(object).getName()});
        }
    }

    /**
     * Audit a "delete" event. Must be called before delete/remove the entity.
     *
     * Usage example:
     * <pre>
     * {@code
     * Audit audit = new Audit(entityManager);
     * audit.delete(id, Person.class);
     * getEntityManager().remove(object);
     * }
     * </pre>
     *
     * @param id Entity Id
     * @param clazz Entity Class
     */
    public void delete(Object id, Class clazz) {
        if (!Audit.isAudit(clazz)) {
            return;
        }
        audit(getPersistedById(id, clazz), null, AuditingType.DELETE);
    }

    /**
     * Audit a "delete" event. Must be called before delete/remove the entity.
     *
     * Usage example:
     * <pre>
     * {@code
     * Audit audit = new Audit(entityManager);
     * audit.delete(object);
     * getEntityManager().remove(object);
     * }
     * </pre>
     *
     * @param object Entity to be audited
     */
    public void delete(Object object) {
        if (!Audit.isAudit(object)) {
            return;
        }
        audit(object, null, AuditingType.DELETE);
    }

    public static String getEntityName(Class entity) {

        if (MAPPED_NAME.get(entity) != null) {
            return MAPPED_NAME.get(entity);
        }

        String name = null;

        Table table = (Table) entity.getAnnotation(Table.class);
        if (table != null && table.name() != null && !table.name().isEmpty()) {
            name = table.name();
        } else {
            Entity entityAnnotation = (Entity) entity.getAnnotation(Entity.class);
            if (entityAnnotation != null && entityAnnotation.name() != null && !entityAnnotation.name().isEmpty()) {
                name = entityAnnotation.name();
            } else {
                name = entity.getSimpleName();
            }
        }

        MAPPED_NAME.put(entity, name);

        return name;
    }

    /**
     * Method to verify if object must be audited
     *
     * @param object
     * @return true if object must be audited
     */
    public static boolean isAudit(Object object) {
        if (object == null) {
            return false;
        }
        return isAudit(EntityUtils.getPersistentClass(object));
    }

    /**
     * Method to verify if class must be audited
     *
     * @param entity
     * @return true if class must be audited
     */
    public static boolean isAudit(Class entity) {
        if (entity.isAnnotationPresent(NotAudited.class)) {
            return false;
        }
        return true;
    }

    /**
     * Persists a Audit
     *
     * @param object
     * @param persisted
     * @param auditingType
     */
    public void audit(Object object, Object persisted, AuditingType auditingType) {

        try {

            if (isEntity(object)) {

                Class entityClass = EntityUtils.getPersistentClass(object);

                Field[] fields = entityClass.getDeclaredFields();
                Method[] methods = entityClass.getDeclaredMethods();
                Method.setAccessible(methods, true);
                Field.setAccessible(fields, true);

                AbstractAuditing auditing = Configuration.getAbstractAuditing();
                auditing.setIdentifier(Long.valueOf(EntityUtils.getId(object).toString()));
                auditing.setEntity(getEntityName(entityClass));
                auditing.setAuditingType(auditingType);
                auditing.setEventDate(new Date());
                if (FacesContext.getCurrentInstance() != null) {
                    auditing.setIp(FacesUtils.getIP());
                }
                auditing.setAuditClass(entityClass);
                AbstractAuditingListener listener = Configuration.getAuditingListener();
                if (listener != null) {
                    listener.onSave(auditing);
                }

                List<AbstractMetadata> metadatas = null;
                boolean auditPersited = false;
                if (auditingType.equals(AuditingType.INSERT) || auditingType.equals(AuditingType.DELETE)) {
                    auditEntityManager.persist(auditing);
                    metadatas = getMetadata(object, null, auditing);
                    auditPersited = true;
                } else if (auditingType.equals(AuditingType.UPDATE)) {
                    metadatas = getMetadata(object, persisted, auditing);
                    if (metadatas != null && !metadatas.isEmpty()) {
                        auditEntityManager.persist(auditing);
                        auditPersited = true;
                    }
                }

                auditing.setMetadatas(metadatas);
                //add to context
                if (auditPersited == true) {
                    AuditContext context = AuditContext.getCurrentInstance();
                    if (context != null && context.isActive()) {
                        context.setAuditing(object, auditing);
                    }
                }

                if (metadatas != null && !metadatas.isEmpty()) {
                    for (AbstractMetadata metadata : metadatas) {
                        auditEntityManager.persist(metadata);
                    }
                }

            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, t.getMessage(), t);
        }

    }

    public List<AbstractMetadata> getMetadata(Object object, Object persisted, AbstractAuditing auditing) throws Exception {
        if(object == null){
            return null;
        }
        List<Method> methodsGet = getMethods(object);
        List<AbstractMetadata> metadatas = new ArrayList<AbstractMetadata>();
        boolean isDelete = auditing.getAuditingType() != null && auditing.getAuditingType().equals(AuditingType.DELETE);
        for (Method method : methodsGet) {
            try {
                Object fieldValue = method.invoke(object);
                Object fieldOld = null;
                if (persisted != null) {
                    fieldOld = method.invoke(persisted);
                    //both null values dont audit
                    if (fieldOld == null && fieldValue == null) {
                        continue;
                    }
                }
                AbstractMetadata metadata = Configuration.getAbstractMetadata();
                if (fieldValue != null && fieldValue.getClass().isAnnotationPresent(Embeddable.class)) {
                    List<AbstractMetadata> embedableMetadata = getMetadata(fieldValue, persisted == null ? null : method.invoke(persisted), auditing);
                    if (embedableMetadata != null && !embedableMetadata.isEmpty()) {
                        metadatas.addAll(embedableMetadata);
                    }
                } else {
                    //if "persisted object" is null then always add metadata
                    boolean addMetadata = persisted == null;
                    //for list types
                    if (method.getReturnType().equals(Collection.class) || method.getReturnType().equals(List.class) || method.getReturnType().equals(Set.class)) { //para as coleções
                        Collection collectionNew = (Collection<Object>) fieldValue;
                        Collection collectionOld = (Collection<Object>) fieldOld;
                        StringBuilder newValue = new StringBuilder();
                        if (fieldOld == null) {
                            if (collectionNew != null) {
                                for (Object item : collectionNew) {
                                    newValue.append(item.toString()).append("; ");
                                }
                                addMetadata = true;
                            }
                        } else {
                            StringBuilder oldValue = new StringBuilder();
                            if ((!(collectionNew instanceof PersistentBag) && !(collectionNew instanceof PersistentCollection)) || isDelete == true) {
                                //diferent size, then add to metadata
                                if (collectionNew != null && collectionOld != null && collectionNew.size() != collectionOld.size()) {
                                    addMetadata = true;
                                } else {
                                    if (collectionNew != null) {
                                        //verify if some item from list has changed
                                        for (Object current : collectionNew) {
                                            if (collectionOld != null && !collectionOld.contains(current)) {
                                                for (Object currentOld : collectionOld) {
                                                    if (!currentOld.equals(current)) {
                                                        addMetadata = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (collectionOld != null) {
                                    for (Object old : collectionOld) {
                                        oldValue.append(old).append("; ");
                                    }
                                }
                                if (collectionNew != null) {
                                    for (Object item : collectionNew) {
                                        newValue.append(item.toString()).append("; ");
                                    }
                                }
                                metadata.setOldValue(oldValue.toString());
                            }
                        }
                        metadata.setNewValue(newValue.toString());
                    } else if (isEntity(method.getReturnType())) {
                        Object newId = EntityUtils.getId(fieldValue);
                        boolean cannotInitialize = !entityManager.contains(fieldValue);

                        //a proxy doesnt has value changed
                        if (!(fieldValue instanceof HibernateProxy && cannotInitialize) || isDelete == true) {
                            /**
                             * One to One cascade ALL
                             */
                            if (isOneToOneCascadeAll(method)) {
                                List<AbstractMetadata> embedableMetadata = null;
                                //add metadata for oneToOne cascade all based on new object
                                if (persisted == null) {
                                    embedableMetadata = getMetadata(fieldValue, null, auditing);
                                } else {
                                    embedableMetadata = getMetadata(fieldValue, getPersisted(fieldValue), auditing);
                                }

                                if (embedableMetadata != null && !embedableMetadata.isEmpty()) {
                                    metadatas.addAll(embedableMetadata);
                                }
                            }

                            Object oldId = null;
                            if (fieldOld instanceof HibernateProxy) {
                                oldId = ((HibernateProxy) fieldOld).getHibernateLazyInitializer().getIdentifier();
                            } else {
                                oldId = EntityUtils.getId(fieldOld);
                            }
                            //set id if is numeric
                            if (oldId != null && oldId.toString() != null && !oldId.toString().isEmpty() && StringUtils.isNumeric(oldId.toString())) {
                                metadata.setOldIdentifier(Long.valueOf(oldId.toString()));
                            }
                            metadata.setOldValue(fieldOld == null ? "" : fieldOld.toString());
                            if ((oldId == null && newId != null) || (oldId != null && newId == null) || (oldId != null && !oldId.equals(newId))) {
                                addMetadata = true;
                            }
                            metadata.setEntity(method.getDeclaringClass().getName());
                            //set id if is numeric
                            if (newId != null && newId.toString() != null && !newId.toString().isEmpty() && StringUtils.isNumeric(newId.toString())) {
                                metadata.setNewIdentifier(Long.valueOf(newId.toString()));
                            }
                            metadata.setNewValue(fieldValue == null ? "" : fieldValue.toString());

                        }
                    } else {
                        if (fieldOld != null) {
                            metadata.setOldValue(getToString(fieldOld));
                        }
                        if (fieldValue != null) {
                            metadata.setNewValue(getToString(fieldValue));
                        }
                        //verify empty String
                        if (fieldValue instanceof String) {
                            if ((fieldOld == null && fieldValue != null && !fieldValue.toString().isEmpty())
                                    || (fieldOld != null && !fieldOld.toString().isEmpty() && fieldValue == null)
                                    || (fieldOld != null && !fieldOld.equals(fieldValue))) {
                                addMetadata = true;
                            }
                        } else {
                            if (!isEquals(fieldOld, fieldValue)) {
                                addMetadata = true;
                            }
                        }
                    }
                    metadata.setField(getMethodName(method));
                    metadata.setAuditing(auditing);
                    if (addMetadata) {
                        //verify size of metadata
                        normalizeValuesWithMaxSize(metadata);
                        metadatas.add(metadata);
                    }
                }
            } catch (Throwable t) {
                logger.log(Level.SEVERE, t.getMessage(), t);
            }
        }
        return metadatas;
    }

    /**
     * Format "newValue" and "oldValue" of AbstractMetada based on size defined
     * in "getMaxSizeValues"
     *
     * @param metadata
     */
    private void normalizeValuesWithMaxSize(AbstractMetadata metadata) {
        if (metadata.getMaxSizeValues() != null) {
            if (metadata.getNewValue() != null) {
                metadata.setNewValue(getValueWithMaxSize(metadata.getNewValue(), metadata.getMaxSizeValues()));
            }
            if (metadata.getOldValue() != null) {
                metadata.setOldValue(getValueWithMaxSize(metadata.getOldValue(), metadata.getMaxSizeValues()));
            }
        }
    }

    private String getValueWithMaxSize(String value, Integer maxSize) {
        if (maxSize == null || value == null || value.length() <= maxSize) {
            return value;
        }
        return value.substring(0, maxSize - 3) + "...";
    }

    public boolean isEquals(Object fieldOld, Object fieldValue) {
        //both null, return true
        if (fieldOld == null && fieldValue == null) {
            return true;
        }
        if (fieldOld == null && fieldValue != null) {
            return false;
        }
        if (fieldValue == null && fieldOld != null) {
            return false;
        }
        if (fieldOld != null && fieldValue != null) {
            if (fieldOld instanceof Date) {
                return ((Date) fieldOld).compareTo((Date) fieldValue) == 0;
            }
            if (fieldOld instanceof Calendar) {
                return ((Calendar) fieldOld).compareTo((Calendar) fieldValue) == 0;
            }
            if (fieldOld instanceof BigDecimal) {
                return ((BigDecimal) fieldOld).compareTo((BigDecimal) fieldValue) == 0;
            }
        }
        if (fieldOld != null) {
            return fieldOld.equals(fieldValue);
        }
        return false;
    }

    public boolean isOneToOneCascadeAll(Method method) throws Exception {
        Boolean isOneToOneAll = MAPPED_ONE_TO_ONE_CASCADE_ALL.get(method);
        if (isOneToOneAll != null) {
            return isOneToOneAll;
        }
        OneToOne oneToOne = method.getAnnotation(OneToOne.class);
        if (oneToOne == null) {
            Field field = getDeclaredField(method.getDeclaringClass(), getMethodName(method));
            if (field != null) {
                oneToOne = field.getAnnotation(OneToOne.class);
            }
        }
        CascadeType[] cascadeTypes = null;
        if (oneToOne != null) {
            cascadeTypes = oneToOne.cascade();
        }
        if (oneToOne != null && cascadeTypes != null && cascadeTypes.length > 0 && Arrays.asList(cascadeTypes).contains(CascadeType.ALL)) {
            isOneToOneAll = true;
        } else {
            isOneToOneAll = false;

        }
        MAPPED_ONE_TO_ONE_CASCADE_ALL.put(method, isOneToOneAll);
        return isOneToOneAll;

    }

    private String getToString(Object object) {
        if (object instanceof Date) {
            return AUDIT_DATE_FORMAT.format(object);
        } else if (object instanceof Calendar) {
            return AUDIT_DATE_FORMAT.format(((Calendar) object).getTime());
        } else {
            return object.toString();
        }
    }

    public List<Method> getMethods(Object objeto) {

        Class entityClass = EntityUtils.getPersistentClass(objeto);
        List<Method> methodGet = MAPPED_METHODS.get(entityClass);

        if (methodGet != null) {
            return methodGet;
        }

        methodGet = new ArrayList<Method>();
        Method methods[] = entityClass.getMethods();

        List exclude = Arrays.asList(EXCLUDED_FIELDS);

        try {
            Field field;
            String fieldName;
            for (int j = 0; j < methods.length; j++) {
                field = null;
                fieldName = "";
                try {
                    if (methods[j] != null && !methods[j].isAnnotationPresent(Transient.class)
                            && !methods[j].isAnnotationPresent(NotAudited.class)
                            && !methods[j].isAnnotationPresent(Id.class) && !exclude.contains(methods[j].getName())) {
                        fieldName = getMethodName(methods[j]);
                    }
                    if (fieldName != null && !fieldName.equals("")) {
                        try {
                            field = getDeclaredField(methods[j].getDeclaringClass(), fieldName);
                        } catch (NoSuchFieldException ex) {
                            continue;
                        }
                        if (field != null && !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(NotAudited.class)
                                && !field.isAnnotationPresent(Id.class)) {
                            methodGet.add(methods[j]);
                        }
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        MAPPED_METHODS.put(entityClass, methodGet);
        return methodGet;
    }

    private String getMethodName(Method method) {
        if (method.getName().startsWith("is")) {
            return method.getName().substring(2, 3).toLowerCase() + method.getName().substring(3);
        } else if (method.getName().startsWith("get")) {
            return method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
        }
        return null;
    }

    private Field getDeclaredField(Class clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        if (field == null && clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
            return getDeclaredField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    /**
     * Verifica se o objeto passado é uma entidade de persistencia
     *
     * @param objeto
     * @return boolean
     */
    public static boolean isEntity(Object objeto) {
        return isEntity(EntityUtils.getPersistentClass(objeto));
    }

    /**
     * Verifica se o objeto passado é uma entidade de persistencia
     *
     * @param clazz
     * @return boolean
     */
    public static boolean isEntity(Class clazz) {
        if (clazz.isAnnotationPresent(Entity.class)) {
            return true;
        }
        return false;
    }
}
