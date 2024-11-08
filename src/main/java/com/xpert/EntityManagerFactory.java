package com.xpert;

import jakarta.persistence.EntityManager;

/**
 *
 * @author ayslan
 */
public interface EntityManagerFactory {
 
    public EntityManager getEntityManager();
    
}
