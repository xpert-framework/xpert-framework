package com.xpert.utils;

import jakarta.persistence.Query;
import java.util.function.Supplier;
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

            Supplier<SelectQueryPlan> buildSelectQueryPlan = () -> ReflectionUtils.invokeMethod(querySqm, "buildSelectQueryPlan");

            SelectQueryPlan<Object> plan = cacheKey != null
                    ? ((QueryImplementor) query).getSession().getFactory().getQueryEngine()
                            .getInterpretationCache()
                            .resolveSelectQueryPlan(cacheKey, (Supplier<SelectQueryPlan<Object>>) (Supplier<?>) buildSelectQueryPlan)
                    : (SelectQueryPlan<Object>) buildSelectQueryPlan.get();

            if (plan instanceof ConcreteSqmSelectQueryPlan selectQueryPlan) {

                Object cacheableSqmInterpretation = ReflectionUtils.getFieldValue(selectQueryPlan, "cacheableSqmInterpretation");

                if (cacheableSqmInterpretation == null) {
                    DomainQueryExecutionContext domainQueryExecutionContext = (DomainQueryExecutionContext) querySqm;
                    cacheableSqmInterpretation = ReflectionUtils.invokeStaticMethod(
                            ReflectionUtils.getMethod(ConcreteSqmSelectQueryPlan.class, "buildCacheableSqmInterpretation",
                                    SqmSelectStatement.class, DomainParameterXref.class, DomainQueryExecutionContext.class),
                            ReflectionUtils.getFieldValue(selectQueryPlan, "sqm"),
                            ReflectionUtils.getFieldValue(selectQueryPlan, "domainParameterXref"),
                            domainQueryExecutionContext
                    );
                }

                if (cacheableSqmInterpretation != null) {
                    JdbcOperationQuerySelect jdbcSelect = ReflectionUtils.getFieldValue(cacheableSqmInterpretation, "jdbcSelect");
                    if (jdbcSelect != null) {
                        return jdbcSelect.getSqlString();
                    }
                }
            }
        }

        return ReflectionUtils.invokeMethod(query, "getQueryString");
    }

    /**
     * 
     * @param query
     * @return 
     */
    private static boolean isValidQuery(Query query) {
        return query instanceof SqmInterpretationsKey.InterpretationsKeySource
                && query instanceof QueryImplementor
                && query instanceof QuerySqmImpl;
    }
    
}
