package com.xpert.utils;

import jakarta.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.query.spi.QueryInterpretationCache;
import org.hibernate.query.spi.SelectQueryPlan;
import org.hibernate.query.sqm.internal.ConcreteSqmSelectQueryPlan;
import org.hibernate.query.sqm.internal.DomainParameterXref;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.internal.SqmInterpretationsKey;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.sql.exec.spi.JdbcOperationQuerySelect;

/**
 * Utility class for extracting SQL from Hibernate queries, using reflection.
 * 
 * @author Arnaldo Jr.
 */
public class SQLExtractorUtils {

    private static final Logger logger = Logger.getLogger(SQLExtractorUtils.class.getName());

    /**
     * Extracts SQL string from a Hibernate Query object.
     * 
     * @param query
     * @return 
     */
    public static String from(Query query) {

        if (isValidQuery(query)) {

            QuerySqmImpl querySqm = (QuerySqmImpl) query;
            QueryInterpretationCache.Key cacheKey = SqmInterpretationsKey.createInterpretationsKey(
                    (SqmInterpretationsKey.InterpretationsKeySource) query);

            Supplier<SelectQueryPlan> buildSelectQueryPlan = () -> invokeMethod(querySqm, "buildSelectQueryPlan");

            SelectQueryPlan<Object> plan = cacheKey != null
                    ? ((QueryImplementor) query).getSession().getFactory().getQueryEngine()
                            .getInterpretationCache()
                            .resolveSelectQueryPlan(cacheKey, (Supplier<SelectQueryPlan<Object>>) (Supplier<?>) buildSelectQueryPlan)
                    : (SelectQueryPlan<Object>) buildSelectQueryPlan.get();

            if (plan instanceof ConcreteSqmSelectQueryPlan selectQueryPlan) {

                Object cacheableSqmInterpretation = getFieldValue(selectQueryPlan, "cacheableSqmInterpretation");

                if (cacheableSqmInterpretation == null) {
                    DomainQueryExecutionContext domainQueryExecutionContext = (DomainQueryExecutionContext) querySqm;
                    cacheableSqmInterpretation = invokeStaticMethod(
                            getMethod(ConcreteSqmSelectQueryPlan.class, "buildCacheableSqmInterpretation",
                                    SqmSelectStatement.class, DomainParameterXref.class, DomainQueryExecutionContext.class),
                            getFieldValue(selectQueryPlan, "sqm"),
                            getFieldValue(selectQueryPlan, "domainParameterXref"),
                            domainQueryExecutionContext
                    );
                }

                if (cacheableSqmInterpretation != null) {
                    JdbcOperationQuerySelect jdbcSelect = getFieldValue(cacheableSqmInterpretation, "jdbcSelect");
                    if (jdbcSelect != null) {
                        return jdbcSelect.getSqlString();
                    }
                }
            }
        }

        return invokeMethod(query, "getQueryString");
    }

    private static boolean isValidQuery(Query query) {
        return query instanceof SqmInterpretationsKey.InterpretationsKeySource
                && query instanceof QueryImplementor
                && query instanceof QuerySqmImpl;
    }

    private static <T> T invokeMethod(Object target, String methodName, Object... parameters) {
        try {
            Method method = getMethod(target, methodName, toClassArray(parameters));
            method.setAccessible(true);
            return (T) method.invoke(target, parameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.log(Level.SEVERE, "Error invoking method: {0}", methodName);
            throw new RuntimeException("Method invocation failed: " + methodName, e);
        }
    }

    private static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = getField(target.getClass(), fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private static <T> T invokeStaticMethod(Method method, Object... parameters) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(null, parameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.severe("Error invoking static method.");
            throw new RuntimeException("Static method invocation failed", e);
        }
    }

    private static Method getMethod(Object target, String methodName, Class... parameterTypes) {
        return getMethod(target.getClass(), methodName, parameterTypes);
    }

    private static Method getMethod(Class targetClass, String methodName, Class... parameterTypes) {
        try {
            return targetClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return targetClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignore) {
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getMethod(targetClass.getSuperclass(), methodName, parameterTypes);
            } else {
                logger.log(Level.SEVERE, "Method [{0}] not found in class [{1}]", new Object[]{methodName, targetClass.getName()});
                throw new RuntimeException("Method not found: " + methodName, e);
            }
        }
    }

    private static Field getField(Class targetClass, String fieldName) {
        try {
            return targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                return targetClass.getField(fieldName);
            } catch (NoSuchFieldException ignore) {
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getField(targetClass.getSuperclass(), fieldName);
            } else {
                logger.log(Level.SEVERE, "Field [{0}] not found in class [{1}]", new Object[]{fieldName, targetClass.getName()});
                throw new RuntimeException("Field not found: " + fieldName, e);
            }
        }
    }

    private static Class[] toClassArray(Object[] parameters) {
        return Arrays.stream(parameters)
                .map(Object::getClass)
                .toArray(Class[]::new);
    }
}
