package com.xpert.audit;

/**
 *
 * @author ayslanms
 */
public interface QueryAuditPersisterFactory {

    /**
     * Return a instance of QueryAuditPersister
     *
     * @return
     */
    QueryAuditPersister getPersister();

}
