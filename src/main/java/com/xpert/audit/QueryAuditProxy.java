package com.xpert.audit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import jakarta.persistence.Query;
import java.io.Serializable;

/**
 *
 * @author ayslanms
 */
public class QueryAuditProxy implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -4534360606513200214L;

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
