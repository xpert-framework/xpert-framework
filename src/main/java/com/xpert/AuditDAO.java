package com.xpert;

import com.xpert.persistence.dao.BaseDAOImpl;
import jakarta.persistence.EntityManager;
import java.io.Serializable;

/**
 *
 * @author ayslan
 * @param <T> Type of DAO
 */
public class AuditDAO<T> extends BaseDAOImpl<T> implements Serializable {

    private static final long serialVersionUID = 2560837941435098665L;

    private EntityManager entityManager;

    public AuditDAO(Class entityClass) {
        super.setEntityClass(entityClass);
    }

    public AuditDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public AuditDAO(Class entityClass, EntityManager entityManager) {
        super.setEntityClass(entityClass);
        this.entityManager = entityManager;
    }

    public AuditDAO() {
    }

    @Override
    public EntityManager getEntityManager() {
        if (entityManager == null) {
            if (Configuration.getAuditEntityManagerFactoryClass() != null) {
                entityManager = Configuration.getAuditEntityManager();
            } else {
                entityManager = Configuration.getEntityManager();
            }

        }
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
