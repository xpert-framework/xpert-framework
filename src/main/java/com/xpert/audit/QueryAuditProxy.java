package com.xpert.audit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.persistence.Query;

/**
 *
 * @author ayslanms
 */
public class QueryAuditProxy implements InvocationHandler {

    private final QueryAuditConfig queryAuditConfig;

    public QueryAuditProxy(QueryAuditConfig queryAuditConfig) {
        this.queryAuditConfig = queryAuditConfig;
    }

    public Query getWrapped() {
        return queryAuditConfig.getQuery();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getSingleResult")
                || method.getName().equals("getResultList")) {
            return new  QueryAudit(queryAuditConfig).persistQuery(getWrapped(), method, args);
        }
        return method.invoke(getWrapped(), args);
    }

}
