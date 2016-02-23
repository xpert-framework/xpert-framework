package com.xpert;

import javax.persistence.EntityManager;

/**
 *
 * @author ayslan
 */
public interface EntityManagerFactory {
 
    public EntityManager getEntityManager();
    
}
