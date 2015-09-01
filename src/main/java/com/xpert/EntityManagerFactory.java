package com.xpert;

import javax.persistence.EntityManager;

/**
 *
 * @author Ayslan
 */
public interface EntityManagerFactory {
 
    public EntityManager getEntityManager();
    
}
