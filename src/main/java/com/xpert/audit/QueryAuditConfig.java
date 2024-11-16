package com.xpert.audit;

import com.xpert.audit.model.AbstractQueryAuditing;
import com.xpert.audit.model.QueryAuditingType;
import com.xpert.persistence.query.QueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;

/**
 *
 * @author ayslanms
 */
public class QueryAuditConfig implements Serializable {

    private static final long serialVersionUID = 2705411883251575813L;

    private AbstractQueryAuditing queryAuditing;
    private EntityManager entityManager;
    private QueryBuilder queryBuilder;
    private Query query;
    private QueryAuditingType auditingType;

    public QueryAuditConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public QueryAuditConfig(EntityManager entityManager, QueryBuilder queryBuilder) {
        this.entityManager = entityManager;
        this.queryBuilder = queryBuilder;
    }

    public QueryAuditConfig(AbstractQueryAuditing queryAuditing, EntityManager entityManager, QueryBuilder queryBuilder) {
        this.queryAuditing = queryAuditing;
        this.entityManager = entityManager;
        this.queryBuilder = queryBuilder;
    }

    public AbstractQueryAuditing getQueryAuditing() {
        return queryAuditing;
    }

    public void setQueryAuditing(AbstractQueryAuditing queryAuditing) {
        this.queryAuditing = queryAuditing;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public QueryAuditingType getAuditingType() {
        return auditingType;
    }

    public void setAuditingType(QueryAuditingType auditingType) {
        this.auditingType = auditingType;
    }

}
