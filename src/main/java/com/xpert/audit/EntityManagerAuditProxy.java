package com.xpert.audit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.io.Serializable;

/**
 *
 * @author ayslanms
 */
public class EntityManagerAuditProxy implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -1946711674121465197L;

    private final QueryAuditConfig queryAuditConfig;

    public EntityManagerAuditProxy(QueryAuditConfig queryAuditConfig) {
        this.queryAuditConfig = queryAuditConfig;
    }

    public EntityManager getWrapped() {
        return queryAuditConfig.getEntityManager();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("createQuery") || method.getName().equals("createNativeQuery")) {
            queryAuditConfig.setAuditingType(QueryAudit.getQueryAuditingType(method));
            return invokeCreateQuery(method, args);
        } else if (method.getName().equals("find")) {
            queryAuditConfig.setAuditingType(QueryAudit.getQueryAuditingType(method));
            return new QueryAudit(queryAuditConfig).persistQuery(getWrapped(), method, args);
        }
        return method.invoke(getWrapped(), args);
    }

    public Object invokeCreateQuery(Method method, Object[] args) throws Throwable {

        //invoke query then Proxy with original EntityManager
        Query query = (Query) method.invoke(getWrapped(), args);
        queryAuditConfig.setQuery(query);

        return new QueryAudit().proxy(queryAuditConfig);
    }

}
