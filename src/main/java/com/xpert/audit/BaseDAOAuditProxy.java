package com.xpert.audit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author ayslanms
 * @param <T>
 */
public class BaseDAOAuditProxy<T> implements InvocationHandler {

    private final T baseDAO;

    public BaseDAOAuditProxy(T baseDAO) {
        this.baseDAO = baseDAO;
    }

    public T getWrapped() {
        return baseDAO;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //invoke audited
        if (method.getName().equals("getEntityManager")) {
            return proxy.getClass().getMethod("getQueryAuditEntityManager").invoke(getWrapped(), args);
        }
        return method.invoke(getWrapped(), args);
    }

}
