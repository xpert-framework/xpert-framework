package com.xpert.persistence.dao;

import com.xpert.persistence.exception.DeleteException;
import com.xpert.persistence.query.QueryBuilder;
import com.xpert.persistence.query.Restriction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.Session;

/**
 * Generic DAO interface
 *
 * @author ayslanms
 * @param <T> Type of BaseDAO
 */
public interface BaseDAO<T> {

    /**
     * Returns the current EntityManager with audit feature
     *
     * @return current javax.persistence.EntityManager
     */
    public EntityManager getEntityManagerQueryAudit();

    /**
     * Returns the current EntityManager
     *
     * @return current javax.persistence.EntityManager
     */
    public EntityManager getEntityManager();

    /**
     * Returns the current Hibernate Session
     *
     * @return current org.hibernate.Session
     */
    public Session getSession();

    /**
     * Create a QueryBuilder with current entityManager from DAO
     *
     * @return
     */
    public QueryBuilder getQueryBuilder();

    /**
     *
     * @return Connection from ConnectionProvider of SessionFactory
     * @throws java.sql.SQLException
     */
    public Connection getConnection() throws SQLException;

    /**
     *
     * @param path
     * @param daoClass
     * @param resultClass
     * @return
     */
    public Query getNativeQueryFromFile(String path, Class daoClass, Class resultClass);

    /**
     *
     * @param path
     * @param daoClass
     * @return
     */
    public Query getNativeQueryFromFile(String path, Class daoClass);

    /**
     * Retrurn Current Entity Class
     *
     * @return
     */
    public Class getEntityClass();

    public void setEntityClass(Class entityClass);

    /**
     * Save a object. This method call entityManager.persist
     *
     * @param object
     */
    public void save(T object);

    /**
     * Save a object. This method call entityManager.persist
     *
     * @param object
     * @param audit Enable audit feature
     */
    public void save(T object, boolean audit);

    /**
     * Call entityManager.persist if @Id is null or entityManager.merge if is
     * not
     *
     * @param object
     */
    public void saveOrMerge(T object);

    /**
     * Call entityManager.persist if @Id is null or entityManager.merge if is
     * not
     *
     * @param object
     * @param audit Enable audit feature
     */
    public void saveOrMerge(T object, boolean audit);

    /**
     * Call saveOrUpdate from Session (Hibernate Implementation)
     *
     * @param object
     */
    public void saveOrUpdate(T object);

    /**
     * Call saveOrUpdate from Session (Hibernate Implementation)
     *
     * @param object
     * @param audit Enable audit feature
     */
    public void saveOrUpdate(T object, boolean audit);

    /**
     * Call update from Session (Hibernate Implementation)
     *
     * @param object
     */
    public void update(T object);

    /**
     * Call update from Session (Hibernate Implementation)
     *
     * @param object
     * @param audit Enable audit feature
     */
    public void update(T object, boolean audit);

    /**
     * Delete a object from database. This method create a HQL to do the
     * deletion
     *
     * @param id Object Id
     * @throws DeleteException
     */
    public void delete(Object id) throws DeleteException;

    /**
     * Delete a object from database. This method create a HQL to do the
     * deletion
     *
     * @param id
     * @param audit Enable audit feature
     * @throws DeleteException
     */
    public void delete(Object id, boolean audit) throws DeleteException;

    /**
     * Delete a object from database. This method create a HQL to do the
     * deletion
     *
     * @param entityClass
     * @param id Object Id
     * @throws DeleteException
     */
    public void delete(Class entityClass, Object id) throws DeleteException;

    /**
     * Delete a object from database. This method create a HQL to do the
     * deletion
     *
     * @param entityClass
     * @param id
     * @param audit Enable audit feature
     * @throws DeleteException
     */
    public void delete(Class entityClass, Object id, boolean audit) throws DeleteException;

    /**
     * Delete a object from database. This method call entityManager.remove()
     *
     * @param object
     * @throws DeleteException
     */
    public void remove(Object object) throws DeleteException;

    /**
     * Delete a object from database. This method call entityManager.remove()
     *
     * @param object
     * @param audit Enable audit feature
     * @throws DeleteException
     */
    public void remove(Object object, boolean audit) throws DeleteException;

    /**
     * Merge a Object. This method call entityManager.remove()
     *
     * @param object
     * @return
     */
    public T merge(T object);

    /**
     * Merge a Object. This method call entityManager.remove()
     *
     * @param object
     * @param audit Enable audit feature
     * @return
     */
    public T merge(T object, boolean audit);

    /**
     * List all objects from Entity
     *
     * @return
     */
    public List<T> listAll();

    /**
     * List all objects from Entity
     *
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAll(boolean audit);

    /**
     * List all objects from Entity
     *
     * @param order
     * @return
     */
    public List<T> listAll(String order);

    /**
     * List all objects from Entity
     *
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAll(String order, boolean audit);

    /**
     * List all objects from Entity
     *
     * @param clazz
     * @param order
     * @return
     */
    public List<T> listAll(Class clazz, String order);

    /**
     * List all objects from Entity
     *
     * @param clazz
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAll(Class clazz, String order, boolean audit);

    /**
     * Count rows from entity
     *
     * @return
     */
    public Long count();

    /**
     * Count rows from entity
     *
     * @param audit Enable audit feature
     * @return
     */
    public Long count(boolean audit);

    /**
     * Count rows from entity
     *
     * @param clazz
     * @return
     */
    public Long count(Class clazz);

    /**
     * Count rows from entity
     *
     * @param clazz
     * @param audit Enable audit feature
     * @return
     */
    public Long count(Class clazz, boolean audit);

    /**
     * Count rows from entity
     *
     * @param parameters
     * @return
     */
    public Long count(Map<String, Object> parameters);

    /**
     * Count rows from entity
     *
     * @param parameters
     * @param audit Enable audit feature
     * @return
     */
    public Long count(Map<String, Object> parameters, boolean audit);

    /**
     * Count rows from entity
     *
     * @param restrictions
     * @return
     */
    public Long count(List<Restriction> restrictions);

    /**
     * Count rows from entity
     *
     * @param restrictions
     * @param audit Enable audit feature
     * @return
     */
    public Long count(List<Restriction> restrictions, boolean audit);

    /**
     * Count rows from entity
     *
     * @param restriction
     * @return
     */
    public Long count(Restriction restriction);

    /**
     * Count rows from entity
     *
     * @param restriction
     * @param audit Enable audit feature
     * @return
     */
    public Long count(Restriction restriction, boolean audit);

    /**
     * Count rows from entity
     *
     * @param property
     * @param value
     * @return
     */
    public Long count(String property, Object value);

    /**
     * Count rows from entity
     *
     * @param property
     * @param value
     * @param audit Enable audit feature
     * @return
     */
    public Long count(String property, Object value, boolean audit);

    /**
     * Count rows from entity
     *
     * @param restrictions
     * @param clazz
     * @return
     */
    public Long count(List<Restriction> restrictions, Class clazz);

    /**
     * Count rows from entity
     *
     * @param restrictions
     * @param clazz
     * @param audit Enable audit feature
     * @return
     */
    public Long count(List<Restriction> restrictions, Class clazz, boolean audit);

    /**
     * Count rows from entity
     *
     * @param restriction
     * @param clazz
     * @return
     */
    public Long count(Restriction restriction, Class clazz);

    /**
     * Count rows from entity
     *
     * @param restriction
     * @param clazz
     * @param audit Enable audit feature
     * @return
     */
    public Long count(Restriction restriction, Class clazz, boolean audit);

    /**
     * Return a object from database by id
     *
     * @param id Object id
     * @return
     */
    public T find(Object id);

    /**
     * Return a object from database by id
     *
     * @param id Object id
     * @param audit Enable audit feature
     * @return
     */
    public T find(Object id, boolean audit);

    /**
     * Return a object from database by id
     *
     * @param entityClass
     * @param id Object id
     * @return
     */
    public T find(Class entityClass, Object id);

    /**
     * Return a object from database by id
     *
     * @param entityClass
     * @param id Object id
     * @param audit Enable audit feature
     * @return
     */
    public T find(Class entityClass, Object id, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Parameters to restrict query results
     * @return a unique object from query
     */
    public T unique(Map<String, Object> restrictions);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Parameters to restrict query results
     * @param audit Enable audit feature
     * @return a unique object from query
     */
    public T unique(Map<String, Object> restrictions, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Restrictions query results
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions);
    
    /**
     * Returns a unique object from query
     *
     * @param restrictions - Restrictions query results
     * @param order
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions,String order);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Restrictions query results
     * @param audit Enable audit feature
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Restrictions query results
     * @param clazz Entity Class
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions, Class clazz);

    /**
     * Returns a unique object from query
     *
     * @param restrictions - Restrictions query results
     * @param clazz Entity Class
     * @param audit Enable audit feature
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions, Class clazz, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param restriction - Restrictions query results
     * @return a unique object from query
     */
    public T unique(Restriction restriction);

    /**
     * Returns a unique object from query
     *
     * @param restriction - Restrictions query results
     * @param audit Enable audit feature
     * @return a unique object from query
     */
    public T unique(Restriction restriction, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param restriction - Restrictions query results
     * @param clazz Entity Class
     * @return a unique object from query
     */
    public T unique(Restriction restriction, Class clazz);

    /**
     * Returns a unique object from query
     *
     * @param restriction - Restrictions query results
     * @param clazz Entity Class
     * @param audit Enable audit feature
     * @return a unique object from query
     */
    public T unique(Restriction restriction, Class clazz, boolean audit);

    /**
     * Returns a unique object from query
     *
     * @param property property name
     * @param value value to restrict
     * @return
     */
    public T unique(String property, Object value);

    /**
     * Returns a unique object from query
     *
     * @param property property name
     * @param value value to restrict
     * @param audit Enable audit feature
     * @return
     */
    public T unique(String property, Object value, boolean audit);

    /**
     * Returns the value of especified attribute
     *
     * @param attributeName atribute name of value
     * @param id id from object
     * @return
     */
    public Object findAttribute(String attributeName, Number id);

    /**
     * Returns the value of especified attribute
     *
     * @param attributeName atribute name of value
     * @param id id from object
     * @param audit Enable audit feature
     * @return
     */
    public Object findAttribute(String attributeName, Number id, boolean audit);

    /**
     * Returns the value of especified attribute
     *
     * @param attributeName
     * @param object
     * @return
     */
    public Object findAttribute(String attributeName, Object object);
    /**
     * Returns the value of especified attribute
     *
     * @param attributeName
     * @param object
     * @param audit Enable audit feature
     * @return
     */
    public Object findAttribute(String attributeName, Object object, boolean audit);

    /**
     * List objects from entity
     *
     * @param attributeName
     * @param id
     * @return
     */
    public Object findList(String attributeName, Number id);
    /**
     * List objects from entity
     *
     * @param attributeName
     * @param id
     * @param audit Enable audit feature
     * @return
     */
    public Object findList(String attributeName, Number id, boolean audit);

    /**
     * List objects from entity
     *
     * @param attributeName
     * @param object
     * @return
     */
    public Object findList(String attributeName, Object object);
    /**
     * List objects from entity
     *
     * @param attributeName
     * @param object
     * @param audit Enable audit feature
     * @return
     */
    public Object findList(String attributeName, Object object, boolean audit);

    /**
     * List objects from entity
     *
     * @param parameters
     * @return
     */
    public List<T> list(Map<String, Object> parameters);
    /**
     * List objects from entity
     *
     * @param parameters
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Map<String, Object> parameters, boolean audit);

    /**
     * List objects from entity
     *
     * @param parameters
     * @param order
     * @return
     */
    public List<T> list(Map<String, Object> parameters, String order);
    /**
     * List objects from entity
     *
     * @param parameters
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Map<String, Object> parameters, String order, boolean audit);

    /**
     * List objects from entity
     *
     * @param restrictions
     * @return
     */
    public List<T> list(List<Restriction> restrictions);
    /**
     * List objects from entity
     *
     * @param restrictions
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(List<Restriction> restrictions, boolean audit);

    /**
     * List objects from entity
     *
     * @param restriction
     * @return
     */
    public List<T> list(Restriction restriction);
    /**
     * List objects from entity
     *
     * @param restriction
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Restriction restriction, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, boolean audit);

    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @return
     */
    public List<T> list(List<Restriction> restrictions, String order);
    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(List<Restriction> restrictions, String order, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, boolean audit);

    /**
     * List objects from entity
     *
     * @param restriction
     * @param order
     * @return
     */
    public List<T> list(Restriction restriction, String order);
    /**
     * List objects from entity
     *
     * @param restriction
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Restriction restriction, String order, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order, boolean audit);

    /**
     * List objects from entity
     *
     * @param args
     * @param order
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<T> list(Map<String, Object> args, String order, Integer firstResult, Integer maxResults);
    /**
     * List objects from entity
     *
     * @param args
     * @param order
     * @param firstResult
     * @param maxResults
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Map<String, Object> args, String order, Integer firstResult, Integer maxResults, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @param firstResult
     * @param maxResults
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @param firstResult
     * @param maxResults
     * @param attributes
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, String attributes);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restriction
     * @param order
     * @param firstResult
     * @param maxResults
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, Restriction restriction, String order, Integer firstResult, Integer maxResults, String attributes, boolean audit);

    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<T> list(Restriction restrictions, String order, Integer firstResult, Integer maxResults);
    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Restriction restrictions, String order, Integer firstResult, Integer maxResults, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, boolean audit);

    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @param attributes
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, String attributes);
    /**
     * List objects from entity
     *
     * @param clazz
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(Class clazz, List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, String attributes, boolean audit);

    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @return
     */
    public List<T> list(List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults);
    /**
     * List objects from entity
     *
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults, boolean audit);

    /**
     * List objects from entity
     *
     * @param property
     * @param value
     * @return
     */
    public List<T> list(String property, Object value);
    /**
     * List objects from entity
     *
     * @param property
     * @param value
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(String property, Object value, boolean audit);

    /**
     * List objects from entity
     *
     * @param property
     * @param value
     * @param order
     * @return
     */
    public List<T> list(String property, Object value, String order);
    /**
     * List objects from entity
     *
     * @param property
     * @param value
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> list(String property, Object value, String order, boolean audit);

    /**
     * Return a initialized object, if object is already initialized returns own
     * object
     *
     * @param <U>
     * @param object
     * @return
     */
    public <U> U getInitialized(U object);

    /**
     *
     * List objects from entity (only the specified attributes)
     *
     * @param attributes
     * @return
     */
    public List<T> listAttributes(String attributes);
    /**
     *
     * List objects from entity (only the specified attributes)
     *
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(String attributes, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param attributes
     * @param order
     * @return
     */
    public List<T> listAttributes(String attributes, String order);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param attributes
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(String attributes, String order, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param property
     * @param value
     * @param attributes
     * @return
     */
    public List<T> listAttributes(String property, Object value, String attributes);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param property
     * @param value
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(String property, Object value, String attributes, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param property
     * @param value
     * @param attributes
     * @param order
     * @return
     */
    public List<T> listAttributes(String property, Object value, String attributes, String order);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param property
     * @param value
     * @param attributes
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(String property, Object value, String attributes, String order, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param args
     * @param attributes
     * @param order
     * @return
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes, String order);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param args
     * @param attributes
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes, String order, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param args
     * @param attributes
     * @return
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param args
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restrictions
     * @param attributes
     * @param order
     * @return
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, String order);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restrictions
     * @param attributes
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, String order, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restrictions
     * @param attributes
     * @return
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restrictions
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restriction
     * @param attributes
     * @param order
     * @return
     */
    public List<T> listAttributes(Restriction restriction, String attributes, String order);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restriction
     * @param attributes
     * @param order
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(Restriction restriction, String attributes, String order, boolean audit);

    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restriction
     * @param attributes
     * @return
     */
    public List<T> listAttributes(Restriction restriction, String attributes);
    /**
     * List objects from entity (only the specified attributes)
     *
     * @param restriction
     * @param attributes
     * @param audit Enable audit feature
     * @return
     */
    public List<T> listAttributes(Restriction restriction, String attributes, boolean audit);
}
