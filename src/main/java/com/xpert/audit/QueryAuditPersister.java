package com.xpert.audit;

import com.xpert.audit.model.AbstractQueryAuditing;

/**
 *
 * @author ayslanms
 */
public interface QueryAuditPersister {

    /**
     * Method called when the object is persisted
     *
     * @param queryAuditing
     */
    public void persist(AbstractQueryAuditing queryAuditing);

    /**
     * Max size of sql statement. If value less or equals than zero, then its unlimited size
     *
     * @return
     */
    public int getSqlStringMaxSize();

    /**
     * Max size of parameters of Query. If value less or equals than zero, then its unlimited size
     *
     * @return
     */
    public int getParametersMaxSize();
}
