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

public interface BaseDAO<T> {

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
     * 
     * @return 
     */
    public QueryBuilder getQueryBuilder();
    
    /**
     * 
     * @return Connection from ConnectionProvider of SessionFactory
     * @throws java.sql.SQLException
     */
    public Connection getConnection() throws SQLException ;

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
     * @param object
     * @param audit Audit Operation
     */
    public void save(T object, boolean audit);

    /**
     * Call entityManager.persist if @Id is null or entityManager.merge if is not
     * 
     * @param object 
     */
    public void saveOrMerge(T object);
    

    /**
     * Call entityManager.persist if @Id is null or entityManager.merge if is not
     * 
     * @param object 
     * @param audit Audit Operation
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
     * @param audit Audit Operation
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
     * @param audit 
     */
    public void update(T object, boolean audit);

    /**
     * Delete a object from database. This method create a HQL to do the deletion
     * 
     * @param id Object Id
     * @throws DeleteException 
     */
    public void delete(Object id) throws DeleteException;

    /**
     * Delete a object from database. This method create a HQL to do the deletion
     * 
     * @param id
     * @param audit Audit Operation
     * @throws DeleteException 
     */
    public void delete(Object id, boolean audit) throws DeleteException;

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
     * @param audit Audit Operation
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
     * @param audit
     * @return 
     */
    public T merge(T object, boolean audit);

    public List<T> listAll();

    public List<T> listAll(String order);

    public List<T> listAll(Class clazz, String order);

    public Long count();

    public Long count(Class clazz);

    public Long count(Map<String, Object> parameters);

    public Long count(List<Restriction> restrictions);

    public Long count(Restriction restriction);

    public Long count(String property, Object value);

    /**
     * Return a object from database by id
     * 
     * @param id Object id
     * @return 
     */
    public T find(Object id);

    public T find(Class entityClass, Object id);

    /**
     * Returns a unique object from query
     * 
     * @param parameters - Parameters to restrict query results
     * @return a unique object from query
     */
    public T unique(Map<String, Object> parameters);

    /**
     * Returns a unique object from query
     * 
     * @param restrictions - Restrictions query results
     * @return a unique object from query
     */
    public T unique(List<Restriction> restrictions);

    public T unique(List<Restriction> restrictions, Class clazz);

    public T unique(Restriction restriction);

    public T unique(Restriction restriction, Class clazz);

    /**
     * Returns a unique object from query
     * 
     * @param property property name
     * @param value value to restrict
     * @return
     */
    public T unique(String property, Object value);

    /**
     * Returns the value of especified attribute
     * 
     * @param attributeName atribute name of value
     * @param id id from object
     * @return 
     */
    public Object findAttribute(String attributeName, Number id);

    /**
     * 
     * @param attributeName
     * @param object
     * @return 
     */
    public Object findAttribute(String attributeName, Object object);

    public Object findList(String attributeName, Number id);

    public Object findList(String attributeName, Object object);

    public List<T> list(Map<String, Object> parameters);

    public List<T> list(Map<String, Object> parameters, String order);

    public List<T> list(List<Restriction> restrictions);

    public List<T> list(Restriction restriction);

    public List<T> list(Class clazz, List<Restriction> restrictions);

    public List<T> list(List<Restriction> restrictions, String order);

    public List<T> list(Class clazz, Restriction restriction);

    public List<T> list(Restriction restriction, String order);

    public List<T> list(Class clazz, List<Restriction> restrictions, String order);

    public List<T> list(Class clazz, Restriction restriction, String order);

    public List<T> list(Map<String, Object> args, String order, Integer firstResult, Integer maxResults);

    /**
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
     * 
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @return 
     */
    public List<T> list(Restriction restrictions, String order, Integer firstResult, Integer maxResults);

    /**
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
     * 
     * @param restrictions
     * @param order
     * @param firstResult
     * @param maxResults
     * @return 
     */
    public List<T> list(List<Restriction> restrictions, String order, Integer firstResult, Integer maxResults);

    /**
     * 
     * @param property
     * @param value
     * @return 
     */
    public List<T> list(String property, Object value);

    /**
     * 
     * @param property
     * @param value
     * @param order
     * @return 
     */
    public List<T> list(String property, Object value, String order);

    /**
     * Return a initialized object, if object is already initialized returns own object
     * 
     * @param <U>
     * @param object
     * @return 
     */
    public <U> U getInitialized(U object);

    /**
     * 
     * @param attributes
     * @return 
     */
    public List<T> listAttributes(String attributes);

    /**
     * 
     * @param attributes
     * @param order
     * @return 
     */
    public List<T> listAttributes(String attributes, String order);

    /**
     * 
     * @param property
     * @param value
     * @param attributes
     * @return 
     */
    public List<T> listAttributes(String property, Object value, String attributes);

    /**
     * 
     * @param property
     * @param value
     * @param attributes
     * @param order
     * @return 
     */
    public List<T> listAttributes(String property, Object value, String attributes, String order);

    /**
     * 
     * @param args
     * @param attributes
     * @param order
     * @return 
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes, String order);

    /**
     * 
     * @param args
     * @param attributes
     * @return 
     */
    public List<T> listAttributes(Map<String, Object> args, String attributes);

    /**
     * 
     * @param restrictions
     * @param attributes
     * @param order
     * @return 
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes, String order);

    /**
     * 
     * @param restrictions
     * @param attributes
     * @return 
     */
    public List<T> listAttributes(List<Restriction> restrictions, String attributes);

    /**
     * 
     * @param restriction
     * @param attributes
     * @param order
     * @return 
     */
    public List<T> listAttributes(Restriction restriction, String attributes, String order);

    /**
     * 
     * @param restriction
     * @param attributes
     * @return 
     */
    public List<T> listAttributes(Restriction restriction, String attributes);
}
