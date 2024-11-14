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
    void persist(AbstractQueryAuditing queryAuditing);

    /**
     * Max size of sql statement. If value less or equals than zero, then its unlimited size
     *
     * @return
     */
    int getSqlStringMaxSize();

    /**
     * Max size of parameters of Query. If value less or equals than zero, then its unlimited size
     *
     * @return
     */
    int getParametersMaxSize();
}
