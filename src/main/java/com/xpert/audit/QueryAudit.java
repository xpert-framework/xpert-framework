package com.xpert.audit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.xpert.Configuration;
import com.xpert.audit.model.AbstractQueryAuditing;
import com.xpert.audit.model.QueryAuditingType;
import com.xpert.faces.utils.FacesUtils;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.query.QueryBuilder;
import com.xpert.persistence.utils.EntityUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.Query;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

/**
 *
 * @author ayslanms
 */
public class QueryAudit {
    
    public static final boolean debug = true;
    private static final Logger logger = Logger.getLogger(QueryAudit.class.getName());
    
    private QueryAuditConfig queryAuditConfig;
    
    public QueryAudit() {
    }
    
    public QueryAudit(QueryAuditConfig queryAuditConfig) {
        this.queryAuditConfig = queryAuditConfig;
    }

    /**
     * Returns a EntityManager proxy with audit feature
     *
     * @param entityManager
     * @param queryBuilder
     * @return
     */
    public EntityManager proxy(EntityManager entityManager, QueryBuilder queryBuilder) {
        QueryAuditConfig config = new QueryAuditConfig(entityManager, queryBuilder);
        return (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(), new Class[]{EntityManager.class}, new EntityManagerAuditProxy(config));
    }

    /**
     * Returns a EntityManager proxy with audit feature
     *
     * @param entityManager
     * @return
     */
    public EntityManager proxy(EntityManager entityManager) {
        QueryAuditConfig config = new QueryAuditConfig(entityManager);
        return (EntityManager) Proxy.newProxyInstance(EntityManager.class.getClassLoader(), new Class[]{EntityManager.class}, new EntityManagerAuditProxy(config));
    }

    /**
     * Returns a EntityManager proxy with audit feature
     *
     * @param config
     * @return
     */
    public Query proxy(QueryAuditConfig config) {
        return (Query) Proxy.newProxyInstance(Query.class.getClassLoader(), new Class[]{Query.class}, new QueryAuditProxy(config));
    }
    
    public static QueryAuditingType getQueryAuditingType(Method method) {
        if (method.getName().equals("find")) {
            return QueryAuditingType.FIND_BY_ID;
        }
        if (method.getName().equals("createQuery")) {
            return QueryAuditingType.QUERY;
        }
        if (method.getName().equals("createNativeQuery")) {
            return QueryAuditingType.NATIVE_QUERY;
        }
        return QueryAuditingType.OTHER;
    }
    
    public Object persistQuery(Object wrapped, Method method, Object[] args) throws Throwable {
        
        if (debug) {
            logger.log(Level.INFO, "Wrapped: {0}, Invoke: {1}, Args: {2}", new Object[]{wrapped.getClass().getName(), method.getName(), Arrays.toString(args)});
        }

        //create before Invoke results
        AbstractQueryAuditing queryAuditing = Configuration.getAbstractQueryAuditing();
        queryAuditConfig.setQueryAuditing(queryAuditing);
        queryAuditing.setAuditingType(queryAuditConfig.getAuditingType());
        queryAuditing.setStartDate(new Date());
        
        Object result = null;
        try {
            result = method.invoke(wrapped, args);
        } catch (NoResultException ex) {
            result = null;
        }
        
        queryAuditing.setEndDate(new Date());
        queryAuditing.calculateTime();
        
        if (debug) {
            logger.log(Level.INFO, "Query executed in {0} milliseconds", queryAuditing.getTimeMilliseconds());
        }

        //find (get Idenfier)
        //JPA find signature - find(Class<T> entityClass, Object primaryKey)
        if (method.getName().equals("find")) {
            if (args.length > 0 && args[0] instanceof Class) {
                queryAuditing.setAuditClass((Class) args[0]);
                queryAuditing.setEntity(Audit.getEntityName((Class) args[0]));
            }
            if (args.length > 1 && args[1] instanceof Number) {
                queryAuditing.setIdentifier(((Number) args[1]).longValue());
            }
        }

        //try to get entity from QueryBuilder
        if (queryAuditing.getEntity() == null && queryAuditConfig.getQueryBuilder() != null && queryAuditConfig.getQueryBuilder().from() != null) {
            queryAuditing.setEntity(Audit.getEntityName(queryAuditConfig.getQueryBuilder().from()));
        }
        
        if (queryAuditConfig.getQuery() != null) {
            queryAuditing.setFirstResult(queryAuditConfig.getQuery().getFirstResult());
            if (queryAuditConfig.getQuery().getMaxResults() != Integer.MAX_VALUE) {
                queryAuditing.setMaxResults(queryAuditConfig.getQuery().getMaxResults());
            }
            if (queryAuditing.getMaxResults() != null && queryAuditing.getMaxResults() > 0) {
                queryAuditing.setPaginatedQuery(true);
            }
        }
        
        QueryAuditPersister queryAuditPersister = Configuration.getQueryAuditPersisterFactory().getPersister();
        buildParameters(queryAuditConfig.getQuery(), queryAuditing, queryAuditPersister);

        //get SQL Query (HQL/JPQL or native)
        if (queryAuditConfig.getQuery() != null) {
            //Hibernate Format SQL
            String sql = new BasicFormatterImpl().format(queryAuditConfig.getQuery().unwrap(org.hibernate.Query.class).getQueryString());
            queryAuditing.setSqlQuery(getValueWithMaxSize(sql, queryAuditPersister.getSqlStringMaxSize()));
        }
        
        if (debug) {
            logger.log(Level.INFO, "SQL Query: {0}", queryAuditing.getSqlQuery());
        }
        
        queryAuditing.setRowsTotal(getRows(result));
        
        if (FacesContext.getCurrentInstance() != null) {
            queryAuditing.setIp(FacesUtils.getIP());
        }
        queryAuditPersister.persist(queryAuditing);
        
        return result;
    }

    /**
     * Build parameters from Query
     *
     * @param query
     * @param queryAuditing
     * @param queryAuditPersister
     */
    public void buildParameters(Query query, AbstractQueryAuditing queryAuditing, QueryAuditPersister queryAuditPersister) {
        
        List<QueryAudit.QueryParameter> parameters = new ArrayList<>();

        //if has id, then is "find" method, set the idenfier
        if (queryAuditing.getIdentifier() != null && queryAuditing.getAuditClass() != null) {
            String idName = EntityUtils.getIdFieldName(queryAuditing.getAuditClass());
            Class idType = EntityUtils.getIdType(queryAuditing.getAuditClass());
            //force a SQL in FIND BY ID
            queryAuditing.setSqlQuery("FROM " + queryAuditing.getAuditClass().getName() + " WHERE " + idName + " =?1 ");
            QueryAudit.QueryParameter queryParameter = new QueryAudit().new QueryParameter(1, idName, idType.getName(), queryAuditing.getIdentifier(), false);
            parameters.add(queryParameter);
        } else {
            if (query != null && query.getParameters() != null) {
                for (Parameter<?> parameter : query.getParameters()) {
                    
                    Integer position = parameter.getPosition();
                    String name = parameter.getName();
                    String type = (parameter.getParameterType() == null ? null : parameter.getParameterType().getName());
                    Object value = query.getParameterValue(parameter);
                    boolean listOfValues = false;
                    if (value != null) {
                        value = getQueryValue(value);
                        if (value instanceof Collection || value instanceof Object[]) {
                            listOfValues = true;
                        }
                    }
                    
                    QueryAudit.QueryParameter queryParameter = new QueryAudit().new QueryParameter(position, name, type, value, listOfValues);
                    
                    parameters.add(queryParameter);
                    
                }
            }
        }
        
        String json = getJsonParameters(parameters);
        
        if (debug) {
            logger.log(Level.INFO, "JSON Parameters: {0}", json);
        }
        
        queryAuditing.setParametersSize(parameters.size());
        if (parameters.size() > 0) {
            queryAuditing.setHasQueryParameter(true);
        }
        queryAuditing.setSqlParameters(getValueWithMaxSize(json, queryAuditPersister.getParametersMaxSize()));
    }
    
    public Object getQueryValue(Object value) {
        if (value != null) {
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                List parameters = new ArrayList<>();
                for (Object object : collection) {
                    parameters.add(getQueryValue(object));
                }
                return parameters;
            }
            if (EntityUtils.isEntity(value.getClass())) {
                return new EntityType(EntityUtils.getId(value), value.toString());
            }
        }
        return value;
        
    }

    /**
     * Return a String with the Query Parameters (using Gson framework)
     *
     * @param parameters
     * @return
     */
    public String getJsonParameters(List<QueryAudit.QueryParameter> parameters) {

        //create JSON object
        Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy").create();
        Type listType = new TypeToken<List<QueryAudit.QueryParameter>>() {
        }.getType();
        
        return parameters.isEmpty() ? null : gson.toJson(parameters, listType);
    }

    /**
     * Return rows of Query result
     *
     * @param result
     * @return
     */
    public static Long getRows(Object result) {
        if (result != null && result instanceof Collection) {
            return (long) ((Collection) result).size();
        }
        if (result != null && result instanceof Object[]) {
            return (long) ((Object[]) result).length;
        }
        if (result != null) {
            return 1L;
        }
        return 0L;
    }

    /**
     * Returns a Striing with a max size (using "..." at the end). If max size
     * "less or equals" than zero, then return the complete String
     *
     * @param value
     * @param maxSize
     * @return
     */
    private static String getValueWithMaxSize(String value, Integer maxSize) {
        if (maxSize == null || value == null || value.length() <= maxSize || maxSize <= 0) {
            return value;
        }
        return value.substring(0, maxSize - 3) + "...";
    }
    
    public class EntityType {
        
        private final Object id;
        private final String value;
        
        public EntityType(Object id, String value) {
            this.id = id;
            this.value = value;
        }
        
    }
    
    public class QueryParameter {
        
        private final Integer position;
        private final String name;
        private final String type;
        private final boolean listOfValues;
        private final Object value;
        
        public QueryParameter(Integer position, String name, String type, Object value, boolean listOfValues) {
            this.position = position;
            this.name = name;
            this.type = type;
            this.value = value;
            this.listOfValues = listOfValues;
        }
        
    }
    
}
