package com.xpert.persistence.dao;

import com.xpert.audit.Audit;
import com.xpert.Configuration;
import com.xpert.audit.QueryAudit;
import com.xpert.persistence.exception.DeleteException;
import com.xpert.persistence.query.QueryBuilder;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.utils.EntityUtils;
import com.xpert.utils.ReflectionUtils;
import com.xpert.utils.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import java.io.Serializable;
import org.hibernate.Hibernate;
import org.hibernate.Hibernate.CollectionInterface;
import static org.hibernate.Hibernate.collection;
import org.hibernate.Session;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentMap;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.collection.spi.PersistentSortedSet;
import org.hibernate.engine.internal.ManagedTypeHelper;
import static org.hibernate.engine.internal.ManagedTypeHelper.asPersistentAttributeInterceptable;
import static org.hibernate.engine.internal.ManagedTypeHelper.isPersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import static org.primefaces.component.resizable.ResizableBase.PropertyKeys.proxy;

public abstract class BaseDAOImpl<T> implements BaseDAO<T>, Serializable {

    private static final long serialVersionUID = 2097589326637061615L;

    private Class entityClass;
    private static final Logger logger = Logger.getLogger(BaseDAOImpl.class.getName());
    private static final Map<ClassField, String> ORDER_BY_MAP = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseDAOImpl() {
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                if (arguments != null && arguments.length > 0) {
                    Object object = arguments[0];
                    if (object instanceof Class<?>) {
                        entityClass = (Class<T>) object;
                    } else {
                        if (object instanceof Class) {
                            entityClass = (Class) object;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    public EntityManager getEntityManager(boolean audit) {
        if (audit) {
            return getEntityManagerQueryAudit();
        }
        return getEntityManager();
    }

    @Override
    public EntityManager getEntityManagerQueryAudit() {
        return new QueryAudit().proxy(getEntityManager());
    }

    @Override
    public Class getEntityClass() {
        return entityClass;
    }

    @Override
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Connection getConnection() throws SQLException {
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) getSession().getSessionFactory();
        return sessionFactoryImpl.getJdbcServices().getBootstrapJdbcConnectionAccess().obtainConnection();
    }

    @Override
    public Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

    @Override
    public QueryBuilder getQueryBuilder() {
        return new QueryBuilder(getEntityManager());
    }

    @Override
    public Query getNativeQueryFromFile(String path, Class daoClass, Class resultClass) {
        return QueryBuilder.createNativeQueryFromFile(getEntityManager(), path, daoClass, resultClass);
    }

    @Override
    public Query getNativeQueryFromFile(String path, Class daoClass) {
        return QueryBuilder.createNativeQueryFromFile(getEntityManager(), path, daoClass);
    }

    private Audit getNewAudit() {
        if (Configuration.getAuditEntityManagerFactoryClass() != null) {
            return new Audit(getEntityManager(), Configuration.getAuditEntityManager());
        } else {
            return new Audit(getEntityManager());
        }
    }

    @Override
    public void save(T object) {
        save(object, Configuration.isAudit());
    }

    @Override
    public void save(T object, boolean audit) {
        getEntityManager().persist(object);
        if (audit) {
            getNewAudit().insert(object);
        }
    }

    @Override
    public void update(T object) {
        update(object, Configuration.isAudit());
    }

    @Override
    public void update(T object, boolean audit) {
        if (audit) {
            getNewAudit().update(object);
        }
        getSession().update(object);
    }

    @Override
    public void saveOrUpdate(T object) {
        saveOrUpdate(object, Configuration.isAudit());
    }

    @Override
    public void saveOrUpdate(T object, boolean audit) {
        boolean persisted = EntityUtils.isPersisted(object);
        if (persisted && audit) {
            getNewAudit().update(object);
        }
        getSession().saveOrUpdate(object);

        if (!persisted && audit) {
            getNewAudit().insert(object);
        }
    }

    @Override
    public void saveOrMerge(T object) {
        saveOrMerge(object, Configuration.isAudit());
    }

    @Override
    public void saveOrMerge(T object, boolean audit) {
        boolean persisted = EntityUtils.isPersisted(object);
        if (!persisted) {
            getEntityManager().persist(object);
            if (audit) {
                getNewAudit().insert(object);
            }
        } else {
            if (audit) {
                getNewAudit().update(object);
            }
            getEntityManager().merge(object);
        }
    }

    @Override
    public T merge(T object) {
        return merge(object, Configuration.isAudit());
    }

    @Override
    public T merge(T object, boolean audit) {
        boolean persisted = EntityUtils.getId(object) != null;
        if (persisted && audit) {
            getNewAudit().update(object);
        }

        object = getEntityManager().merge(object);

        if (!persisted && audit) {
            getNewAudit().insert(object);
        }
        return object;
    }

    @Override
    public void delete(Object id) throws DeleteException {
        delete(id, Configuration.isAudit());
    }

    @Override
    public void delete(Object id, boolean audit) throws DeleteException {
        delete(getEntityClass(), id, audit);
    }

    @Override
    public void delete(Class entityClass, Object id) throws DeleteException {
        delete(entityClass, id, Configuration.isAudit());
    }

    @Override
    public void delete(Class entityClass, Object id, boolean audit) throws DeleteException {

        try {
            if (audit) {
                getNewAudit().delete(id, getEntityClass());
            }
            Query query = getEntityManager().createQuery("DELETE FROM " + entityClass.getName() + " WHERE " + EntityUtils.getIdFieldName(entityClass) + " = ?1 ");
            query.setParameter(1, id);
            query.executeUpdate();
        } catch (Exception ex) {
            if (ex instanceof ConstraintViolationException || ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new DeleteException("Object from class " + entityClass + " with ID: " + id + " cannot be deleted");
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    @Override
    public void remove(Object object) throws DeleteException {
        remove(object, Configuration.isAudit());
    }

    @Override
    public void remove(Object object, boolean audit) throws DeleteException {
        try {
            if (!getEntityManager().contains(object)) {
                object = getEntityManager().merge(object);
            }
            if (audit) {
                getNewAudit().delete(object);
            }
            getEntityManager().remove(object);
            getEntityManager().flush();
        } catch (Exception ex) {
            if (ex instanceof ConstraintViolationException || ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new DeleteException("Object from class " + getEntityClass() + " with ID: " + EntityUtils.getId(object) + " cannot be deleted");
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    @Override
    public T find(Object id) {
        return find(id, false);
    }

    @Override
    public T find(Object id, boolean audit) {
        return (T) getEntityManager(audit).find(getEntityClass(), id);
    }

    @Override
    public T find(Class entityClass, Object id) {
        return find(entityClass, id, false);
    }

    @Override
    public T find(Class entityClass, Object id, boolean audit) {
        return (T) getEntityManager(audit).find(entityClass, id);
    }

    @Override
    public List<T> listAll() {
        return listAll(false);
    }

    @Override
    public List<T> listAll(boolean audit) {
        return listAll(null, audit);
    }

    @Override
    public List<T> listAll(String order) {
        return listAll(order, false);
    }

    @Override
    public List<T> listAll(String order, boolean audit) {
        return listAll(getEntityClass(), order, audit);
    }

    @Override
    public List<T> listAll(Class clazz, String order) {
        return listAll(clazz, order, false);
    }

    @Override
    public List<T> listAll(Class clazz, String order, boolean audit) {
        Query query = getQueryBuilder()
                .audit(audit)
                .from(clazz)
                .orderBy(order)
                .createQuery();
        return query.getResultList();
    }

    @Override
    public Object findAttribute(String attributeName, Number id) {
        return findAttribute(attributeName, id, false);

    }

    @Override
    public Object findAttribute(String attributeName, Number id, boolean audit) {

        QueryBuilder builder = getQueryBuilder().audit(audit);

        return builder.select("o." + attributeName)
                .from(getEntityClass(), "o")
                .add("o." + EntityUtils.getIdFieldName(getEntityClass()), id)
                .getSingleResult();

    }

    @Override
    public Object findAttribute(String attributeName, Object object) {
        return findAttribute(attributeName, object, false);
    }

    @Override
    public Object findAttribute(String attributeName, Object object, boolean audit) {
        return findAttribute(attributeName, (Number) EntityUtils.getId(object), audit);
    }

    @Override
    public Object findList(String attributeName, Number id) {
        return findList(attributeName, id, false);

    }

    @Override
    public Object findList(String attributeName, Number id, boolean audit) {

        QueryBuilder builder = getQueryBuilder().audit(audit);

        return builder.select("o." + attributeName)
                .from(getEntityClass(), "o")
                .add("o." + EntityUtils.getIdFieldName(getEntityClass()), id)
                .getResultList();

    }

    @Override
    public Object findList(String attributeName, Object object) {
        return findList(attributeName, object, false);
    }

    @Override
    public Object findList(String attributeName, Object object, boolean audit) {
        return findAttribute(attributeName, (Number) EntityUtils.getId(object), audit);
    }

    @Override
    public T unique(Map<String, Object> restrictions) {
        return unique(restrictions, false);
    }

    @Override
    public T unique(Map<String, Object> restrictions, boolean audit) {
        Query query = getQueryBuilder().audit(false)
                .from(getEntityClass())
                .add(restrictions).createQuery()
                .setMaxResults(1);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public T unique(Restriction restriction) {
        return unique(restriction, false);
    }

    @Override
    public T unique(Restriction restriction, boolean audit) {
        return unique(getRestrictions(restriction), getEntityClass(), audit);
    }

    @Override
    public T unique(List<Restriction> restrictions) {
        return unique(restrictions, false);
    }

    @Override
    public T unique(List<Restriction> restrictions, boolean audit) {
        return unique(restrictions, getEntityClass(), audit);
    }

    @Override
    public T unique(Restriction restriction, Class clazz) {
        return unique(restriction, clazz, false);
    }

    @Override
    public T unique(Restriction restriction, Class clazz, boolean audit) {
        return unique(getRestrictions(restriction), clazz, audit);
    }

    @Override
    public T unique(String property, Object value) {
        return unique(property, value, false);
    }

    @Override
    public T unique(String property, Object value, boolean audit) {
        return unique(new Restriction(property, value), getEntityClass(), audit);
    }

    @Override
    public T unique(List<Restriction> restrictions, Class clazz) {
        return unique(restrictions, clazz, false);
    }

    @Override
    public T unique(List<Restriction> restrictions, Class clazz, boolean audit) {
        Query query = getQueryBuilder()
                .audit(audit)
                .from(clazz)
                .add(restrictions).createQuery();
        query.setMaxResults(1);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
    @Override
    public T unique(List<Restriction> restrictions, String order) {
        Query query = getQueryBuilder()
                .audit(false)
                .from(getEntityClass())
                .add(restrictions)
                .orderBy(order)
                .createQuery();
        query.setMaxResults(1);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<T> list(Map<String, Object> restrictions, String order) {
        return list(restrictions, order, false);
    }

    @Override
    public List<T> list(Map<String, Object> restrictions, String order, boolean audit) {
        return list(restrictions, order, null, null, audit);
    }

    @Override
    public List<T> list(Map<String, Object> restrictions) {
        return list(restrictions, false);
    }

    @Override
    public List<T> list(Map<String, Object> restrictions, boolean audit) {
        return list(restrictions, null, audit);
    }

    @Override
    public List<T> list(Map<String, Object> restrictions, String order, Integer firstResult, Integer maxResults) {
        return list(restrictions, order, firstResult, maxResults, false);
    }

    @Override
    public List<T> list(Map<String, Object> restrictions, String order, Integer firstResult, Integer maxResults, boolean audit) {
        Query query = getQueryBuilder()
                .audit(audit)
                .from(getEntityClass())
                .add(restrictions)
                .orderBy(order)
                .createQuery();

        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        return query.getResultList();
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions) {
        return list(clazz, restrictions, false);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, boolean audit) {
        return list(clazz, restrictions, null, audit);
    }

    @Override
    public List<T> list(List<Restriction> restrictions) {
        return list(restrictions, false);
    }

    @Override
    public List<T> list(List<Restriction> restrictions, boolean audit) {
        return list(getEntityClass(), restrictions, audit);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction) {
        return list(clazz, restriction, false);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, boolean audit) {
        return list(clazz, getRestrictions(restriction), null, audit);
    }

    @Override
    public List<T> list(String property, Object value) {
        return list(property, value, false);
    }

    @Override
    public List<T> list(String property, Object value, boolean audit) {
        return list(getEntityClass(), new Restriction(property, value), audit);
    }

    @Override
    public List<T> list(String property, Object value, String order) {
        return list(property, value, order, false);
    }

    @Override
    public List<T> list(String property, Object value, String order, boolean audit) {
        return list(getEntityClass(), new Restriction(property, value), order, audit);
    }

    @Override
    public List<T> list(Restriction restriction) {
        return list(restriction, false);
    }

    @Override
    public List<T> list(Restriction restriction, boolean audit) {
        return list(getEntityClass(), getRestrictions(restriction), audit);
    }

    @Override
    public List<T> listAttributes(String attributes) {
        return listAttributes(attributes, false);
    }

    @Override
    public List<T> listAttributes(String attributes, boolean audit) {
        return listAttributes(attributes, null, audit);
    }

    @Override
    public List<T> listAttributes(String attributes, String order) {
        return listAttributes(attributes, order, false);
    }

    @Override
    public List<T> listAttributes(String attributes, String order, boolean audit) {
        return list(getEntityClass(), (List) null, order, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(Map<String, Object> restrictions, String attributes, String order) {
        return listAttributes(restrictions, attributes, order, false);
    }

    @Override
    public List<T> listAttributes(Map<String, Object> restrictions, String attributes, String order, boolean audit) {
        return list(getEntityClass(), getRestrictionsFromMap(restrictions), order, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(Map<String, Object> restrictions, String attributes) {
        return listAttributes(restrictions, attributes, false);
    }

    @Override
    public List<T> listAttributes(Map<String, Object> restrictions, String attributes, boolean audit) {
        return list(getEntityClass(), getRestrictionsFromMap(restrictions), null, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, String order) {
        return listAttributes(restrictions, attributes, order, false);
    }

    @Override
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, String order, boolean audit) {
        return list(getEntityClass(), restrictions, order, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(List<Restriction> restrictions, String attributes) {
        return listAttributes(restrictions, attributes, false);
    }

    @Override
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, boolean audit) {
        return list(getEntityClass(), restrictions, null, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(Restriction restriction, String attributes, String order) {
        return listAttributes(restriction, attributes, order, false);
    }

    @Override
    public List<T> listAttributes(Restriction restriction, String attributes, String order, boolean audit) {
        return list(getEntityClass(), getRestrictions(restriction), order, null, null, attributes, audit);
    }

    @Override
    public List<T> listAttributes(Restriction restriction, String attributes) {
        return listAttributes(restriction, attributes, false);
    }

    @Override
    public List<T> listAttributes(Restriction restriction, String attributes, boolean audit) {
        return list(getEntityClass(), getRestrictions(restriction), null, null, null, attributes, audit);
    }

    @Override
    public List listAttributes(String property, Object value, String attributes) {
        return listAttributes(property, value, attributes, false);
    }

    @Override
    public List listAttributes(String property, Object value, String attributes, boolean audit) {
        return listAttributes(new Restriction(property, value), attributes, audit);
    }

    @Override
    public List listAttributes(String property, Object value, String attributes, String order) {
        return listAttributes(property, value, attributes, order, false);
    }

    @Override
    public List listAttributes(String property, Object value, String attributes, String order, boolean audit) {
        return listAttributes(new Restriction(property, value), attributes, order, audit);
    }

    @Override
    public List<T> list(List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults) {
        return list(restrictions, order, firstResult, maxResults, false);
    }

    @Override
    public List<T> list(List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, boolean audit) {
        return list(getEntityClass(), restrictions, order, firstResult, maxResults, audit);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults) {
        return list(clazz, restrictions, order, firstResult, maxResults, false);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, boolean audit) {
        return list(clazz, restrictions, order, firstResult, maxResults, null, audit);
    }

    @Override
    public List<T> list(Restriction restriction, String order, Integer firstResult, Integer maxResults) {
        return list(restriction, order, firstResult, maxResults, false);
    }

    @Override
    public List<T> list(Restriction restriction, String order, Integer firstResult, Integer maxResults, boolean audit) {
        return list(getEntityClass(), getRestrictions(restriction), order, firstResult, maxResults, audit);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults) {
        return list(clazz, restriction, order, firstResult, maxResults, false);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, boolean audit) {
        return list(clazz, getRestrictions(restriction), order, firstResult, maxResults, null);
    }

    @Override
    public List<T> list(List<Restriction> restrictions, String order) {
        return list(restrictions, order, false);
    }

    @Override
    public List<T> list(List<Restriction> restrictions, String order, boolean audit) {
        return list(getEntityClass(), restrictions, order, audit);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order) {
        return list(clazz, restrictions, order, false);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, boolean audit) {
        return list(clazz, restrictions, order, null, null, audit);
    }

    @Override
    public List<T> list(Restriction restriction, String order) {
        return list(restriction, order, false);
    }

    @Override
    public List<T> list(Restriction restriction, String order, boolean audit) {
        return list(getEntityClass(), getRestrictions(restriction), order, audit);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order) {
        return list(clazz, restriction, order, false);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order, boolean audit) {
        return list(clazz, getRestrictions(restriction), order, null, null, audit);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, String attributes) {
        return list(clazz, getRestrictions(restriction), order, firstResult, maxResults, attributes);
    }

    @Override
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, String attributes, boolean audit) {
        return list(clazz, getRestrictions(restriction), order, firstResult, maxResults, attributes, audit);
    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, String attributes) {
        return list(clazz, restrictions, order, firstResult, maxResults, attributes, false);

    }

    @Override
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, String attributes, boolean audit) {

        return getQueryBuilder()
                .audit(audit)
                .select(attributes)
                .from(clazz)
                .add(restrictions)
                .orderBy(order)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList(clazz);

    }

    @Override
    public Long count(Map<String, Object> restrictions) {
        return count(restrictions, false);
    }

    @Override
    public Long count(Map<String, Object> restrictions, boolean audit) {
        return getQueryBuilder()
                .audit(audit)
                .from(getEntityClass())
                .add(restrictions)
                .count();
    }

    @Override
    public Long count(String property, Object value) {
        return count(property, value, false);
    }

    @Override
    public Long count(String property, Object value, boolean audit) {
        return count(new Restriction(property, value), audit);
    }

    @Override
    public Long count(Restriction restriction) {
        return count(restriction, false);
    }

    @Override
    public Long count(Restriction restriction, boolean audit) {
        return count(getRestrictions(restriction), audit);
    }

    @Override
    public Long count(List<Restriction> restrictions) {
        return count(restrictions, false);
    }

    @Override
    public Long count(List<Restriction> restrictions, boolean audit) {
        return getQueryBuilder()
                .audit(audit)
                .from(getEntityClass())
                .add(restrictions)
                .count();
    }

    @Override
    public Long count() {
        return count(false);
    }

    @Override
    public Long count(boolean audit) {
        return count(getEntityClass(), audit);
    }

    @Override
    public Long count(Class clazz) {
        return count(clazz, false);
    }

    @Override
    public Long count(Class clazz, boolean audit) {
        return count((List) null, clazz, audit);
    }

    @Override
    public Long count(List<Restriction> restrictions, Class clazz) {
        return count(restrictions, clazz, false);
    }

    @Override
    public Long count(List<Restriction> restrictions, Class clazz, boolean audit) {
        return getQueryBuilder()
                .audit(audit)
                .from(clazz)
                .add(restrictions)
                .count();
    }

    @Override
    public Long count(Restriction restriction, Class clazz) {
        return count(restriction, clazz, false);
    }

    @Override
    public Long count(Restriction restriction, Class clazz, boolean audit) {
        return getQueryBuilder()
                .audit(audit)
                .from(clazz)
                .add(restriction)
                .count();
    }

    @Override
    public <U> U getInitialized(U object) {

        if (object == null) {
            return null;
        }

        if (Hibernate.isInitialized(object)) {
            return handleInitializedObject(object);
        } else {
            return handleUninitializedObject(object);
        }

    }

    @SuppressWarnings("unchecked")
    private <U> U handleInitializedObject(U object) {
        if (ManagedTypeHelper.isHibernateProxy(object)) {
            HibernateProxy hibernateProxy = ManagedTypeHelper.asHibernateProxy(object);
            if (hibernateProxy != null) {
                LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();
                return (U) lazyInitializer.getImplementation();
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    private <U> U handleUninitializedObject(U object) {

        if (isPersistentAttributeInterceptable(object)) {
            final PersistentAttributeInterceptor interceptor = asPersistentAttributeInterceptable(object).$$_hibernate_getInterceptor();
            if (interceptor instanceof EnhancementAsProxyLazinessInterceptor lazinessInterceptor) {
                Object identifier = lazinessInterceptor.getEntityKey().getIdentifier();
                Class<?> entity = lazinessInterceptor.getEntityKey().getPersister().getMappedClass();
                return (U) getEntityManager().find(entity, identifier);
            }
        }

        if (object instanceof PersistentCollection persistentCollection) {
            return (U) handlePersistentCollection(persistentCollection);
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private <U> U handlePersistentCollection(PersistentCollection collection) {
        
        String role = collection.getRole();
        Object owner = collection.getOwner();
        String fieldName = role.substring(role.lastIndexOf(".") + 1);
        String orderBy = getOrderBy(fieldName, owner.getClass());

        String queryString = buildQueryString(owner.getClass().getName(), fieldName, orderBy);
        Query query = getEntityManager().createQuery(queryString);
        query.setParameter(1, owner);

        return (U) query.getResultList();
    }

    private String buildQueryString(String ownerClassName, String fieldName, String orderBy) {

        StringBuilder query = new StringBuilder();
        query.append(" SELECT c ");
        query.append(" FROM ").append(ownerClassName).append(" o ");
        query.append(" JOIN o.").append(fieldName).append(" c ");
        query.append(" WHERE o = ?1 ");

        if (orderBy != null && !orderBy.isEmpty()) {
            query.append(" ORDER BY ").append(getOrderByWithAlias("c", orderBy));
        }

        return query.toString();
    }

    /**
     * Normalize a order by. Example: @OrderBy(value = "descricao, id DESC")
     * With alias "c" should be ORDER BY c.descricao, c.id DESC
     *
     *
     * @param alias
     * @param orderBy
     * @return
     */
    public String getOrderByWithAlias(String alias, String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return "";
        }
        String[] parts = orderBy.split(",");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            if (alias != null && !alias.isEmpty()) {
                builder.append(alias).append(".");
            }
            builder.append(parts[i].trim());
        }
        return builder.toString();
    }

    /**
     * Get value of OrderBy in class
     *
     * @param fieldName
     * @param entity
     * @return
     */
    private String getOrderBy(String fieldName, Class entity) {
        String orderBy = null;

        //try find in cache
        ClassField classField = new ClassField(entity, fieldName);
        if (ORDER_BY_MAP.containsKey(classField)) {
            return ORDER_BY_MAP.get(classField);
        }

        try {
            Field field = ReflectionUtils.getDeclaredField(entity, fieldName);
            if (field != null) {
                OrderBy annotation = field.getAnnotation(OrderBy.class);
                orderBy = getOrderByFromAnnotaion(annotation, field, null);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (orderBy == null || orderBy.isEmpty()) {
            try {
                Method method = entity.getMethod("get" + StringUtils.getUpperFirstLetter(fieldName));
                if (method != null) {
                    OrderBy annotation = method.getAnnotation(OrderBy.class);
                    orderBy = getOrderByFromAnnotaion(annotation, null, method);
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        ORDER_BY_MAP.put(new ClassField(entity, fieldName), orderBy);

        return orderBy;
    }

    /**
     * Get filed in @OrderBy annotation
     *
     * @param annotation
     * @param field
     * @param method
     * @return
     */
    private String getOrderByFromAnnotaion(OrderBy annotation, Field field, Method method) {
        String orderBy = null;
        if (annotation != null) {
            String value = annotation.value();
            if (value != null && !value.isEmpty()) {
                orderBy = value;
            } else {
                Type realType = null;
                if (field != null) {
                    if (field.getGenericType() != null && field.getGenericType() instanceof ParameterizedType) {
                        if (((ParameterizedType) field.getGenericType()).getActualTypeArguments().length > 0) {
                            realType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        }
                    }
                }
                if (method != null) {
                    if (method.getGenericReturnType() != null && method.getGenericReturnType() instanceof ParameterizedType) {
                        if (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments().length > 0) {
                            realType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        }
                    }
                }
                if (realType == null) {
                    return null;
                }
                orderBy = EntityUtils.getIdFieldName((Class) realType);
            }
        }
        return orderBy;
    }

    /**
     * Convert a Map into a List of Restriction
     *
     * @param args
     * @return
     */
    private List<Restriction> getRestrictionsFromMap(Map<String, Object> args) {
        List<Restriction> restrictions = new ArrayList<>();
        for (Entry e : args.entrySet()) {
            restrictions.add(new Restriction(e.getKey().toString(), e.getValue()));
        }
        return restrictions;
    }

    /**
     * Return a list with a single restriction
     *
     * @param restriction
     * @return
     */
    private List<Restriction> getRestrictions(Restriction restriction) {
        List<Restriction> restrictions = null;
        if (restriction != null) {
            restrictions = new ArrayList<>();
            restrictions.add(restriction);
        }
        return restrictions;
    }
}
