package com.xpert;

import com.xpert.persistence.dao.BaseDAOImpl;
import javax.persistence.EntityManager;

/**
 *
 * @author Ayslan
 * @param <T> Type of DAO
 */
public class DAO<T> extends BaseDAOImpl<T> {

    private EntityManager entityManager;

    public DAO(Class entityClass) {
        super.setEntityClass(entityClass);
    }

    public DAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DAO(Class entityClass, EntityManager entityManager) {
        super.setEntityClass(entityClass);
        this.entityManager = entityManager;
    }

    public DAO() {
    }

    @Override
    public EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = Configuration.getEntityManager();
        }
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    
}
