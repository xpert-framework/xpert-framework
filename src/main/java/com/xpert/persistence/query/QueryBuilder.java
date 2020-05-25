package com.xpert.persistence.query;

import com.xpert.persistence.exception.QueryFileNotFoundException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A JPQL/HQL based builder. This class is a collection of utility methods to
 * build JPA query
 *
 * @author ayslan
 */
public class QueryBuilder {

    private String orderBy;
    private String groupBy;
    private String attributes;
    private String aggregate;
    /**
     * to be used in SUM, MAX, MIN, COUNT
     */
    private String attribute;
    private Class from;
    private String alias;
    private final JoinBuilder joins = new JoinBuilder();
    private final List<Restriction> restrictions = new ArrayList<>();
    private final List<Restriction> having = new ArrayList<>();
    private List<Restriction> normalizedRestrictions = new ArrayList<>();
    private List<Restriction> normalizedHaving = new ArrayList<>();
    private QueryType type;
    private final EntityManager entityManager;
    private Integer maxResults;
    private Integer firstResult;
    private List<QueryParameter> parameters = new ArrayList<>();
    private boolean debug;
    private static final Logger logger = Logger.getLogger(QueryBuilder.class.getName());

    public QueryBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Defines the "from" clusule
     *
     * @param from Class to be used in select
     * @return Current QueryBuilder
     */
    public QueryBuilder from(Class from) {
        this.from = from;
        return this;
    }

    /**
     * Defines the "from" clusule
     *
     * @param from Class to be used in select
     * @param alias An alias for the entity
     * @return Current QueryBuilder
     */
    public QueryBuilder from(Class from, String alias) {
        this.from = from;
        this.alias = alias;
        return this;
    }

    /**
     * Defines the query "order by"
     *
     * @param order A String with the order. It can be used multiple fields
     * "field1, field2" and "asc" and "desc" can be defined "field1 desc,
     * field2"
     * @return Current QueryBuilder
     */
    public QueryBuilder orderBy(String... order) {
        if (order == null) {
            this.orderBy = null;
        } else {
            this.orderBy = StringUtils.join(order, ", ");
        }
        return this;
    }

    /**
     * Set groupBy , attributes and orderBy to make a cleaner query
     *
     * @param by
     * @return
     */
    public QueryBuilder by(String... by) {
        if (by == null) {
            this.groupBy = null;
            this.attributes = null;
            this.orderBy = null;
        } else {
            this.groupBy = StringUtils.join(by, ", ");
            this.attributes = StringUtils.join(by, ", ");
            this.orderBy = StringUtils.join(by, ", ");
        }
        return this;
    }

    /**
     * Defines the query "order by"
     *
     * @param group A String with the grou by clause. It can be used multiple
     * fields field2"
     * @return Current QueryBuilder
     */
    public QueryBuilder groupBy(String... group) {
        if (group == null) {
            this.groupBy = null;
        } else {
            this.groupBy = StringUtils.join(group, ", ");
        }
        return this;
    }

    /**
     * Defines the query "order by"
     *
     * @param order A list of string with the order. It can be used multiple
     * fields "field1, field2" and "asc" and "desc" can be defined "field1 desc,
     * field2"
     * @return Current QueryBuilder
     */
    public QueryBuilder orderBy(Collection<String> order) {
        if (order == null) {
            this.orderBy = null;
        } else {
            this.orderBy = StringUtils.join(order, ", ");
        }
        return this;
    }

    /**
     * Defines the query "order by"
     *
     * @param group A String with the grou by clause. It can be used multiple
     * fields field2"
     * @return Current QueryBuilder
     */
    public QueryBuilder groupBy(Collection<String> group) {
        if (group == null) {
            this.groupBy = null;
        } else {
            this.groupBy = StringUtils.join(group, ", ");
        }
        return this;
    }

    /**
     * Add restrictions to HAVING CLAUSE
     *
     * @param restriction
     * @return
     */
    public QueryBuilder having(Restriction restriction) {
        if (restriction != null) {
            this.having.add(restriction);
        }
        return this;
    }

    /**
     * Add restrictions to HAVING CLAUSE
     *
     * @param property
     * @param restrictionType
     * @param value
     * @return
     */
    public QueryBuilder having(String property, RestrictionType restrictionType, Object value) {
        this.having.add(new Restriction(property, restrictionType, value));
        return this;
    }

    /**
     * Defines if the this builder will be logged
     *
     * @return
     */
    public QueryBuilder debug() {
        this.debug = true;
        return this;
    }

    /**
     * Defines if the this builder will be logged
     *
     * @param debug
     * @return
     */
    public QueryBuilder debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    private void loadNormalizedRestrictions() {
        normalizedRestrictions = RestrictionsNormalizer.getNormalizedRestrictions(from, restrictions, alias);
        normalizedHaving = RestrictionsNormalizer.getNormalizedRestrictions(from, having, null);
    }

    /**
     *
     * Retruns String of the part after "WHERE" from JPQL generated
     *
     * @param restrictions
     * @return String of the part after "WHERE" from JPQL generated
     */
    public String getQueryStringFromRestrictions(List<Restriction> restrictions) {

        int currentParameter = 1;
        StringBuilder queryString = new StringBuilder();
        boolean processPropertyAndValue = false;
        Restriction lastRestriction = null;

        for (Restriction restriction : restrictions) {

            if (lastRestriction != null) {
                if (lastRestriction.getRestrictionType().equals(RestrictionType.OR)) {
                    queryString.append(" OR ");
                    processPropertyAndValue = true;
                } else {
                    if (!lastRestriction.getRestrictionType().equals(RestrictionType.START_GROUP)
                            && !restriction.getRestrictionType().equals(RestrictionType.OR)
                            && !restriction.getRestrictionType().equals(RestrictionType.END_GROUP)) {
                        queryString.append(" AND ");
                    }
                    processPropertyAndValue = true;
                }
            }

            if (restriction.getRestrictionType().equals(RestrictionType.START_GROUP)
                    || restriction.getRestrictionType().equals(RestrictionType.END_GROUP)) {
                queryString.append(restriction.getRestrictionType().getSymbol());
                processPropertyAndValue = false;
            } else if (restriction.getRestrictionType().equals(RestrictionType.OR)) {
                processPropertyAndValue = false;
            } else {
                processPropertyAndValue = true;
            }

            lastRestriction = restriction;

            if (processPropertyAndValue) {
                String propertyName = restriction.getProperty();
                if (restriction.getCastAs() != null && !restriction.getCastAs().isEmpty()) {
                    propertyName = "CAST(" + propertyName + " AS " + restriction.getCastAs() + ")";
                }
                //custom query string
                if (restriction.getRestrictionType().equals(RestrictionType.QUERY_STRING)) {
                    queryString.append(" (").append(restriction.getProperty()).append(") ");
                } else {
                    if (restriction.getRestrictionType().equals(RestrictionType.LIKE) || restriction.getRestrictionType().equals(RestrictionType.NOT_LIKE)) {
                        if (restriction.isIlike()) {
                            queryString.append("UPPER(").append(propertyName).append(")").append(" ");
                        } else {
                            queryString.append(propertyName).append(" ");
                        }
                    } else if (restriction.getTemporalType() != null && restriction.getTemporalType().equals(TemporalType.DATE)) {
                        queryString.append("CAST(").append(propertyName).append(" AS date)").append(" ");
                    } else {
                        //member of has a own logic
                        if (!restriction.getRestrictionType().equals(RestrictionType.MEMBER_OF) && !restriction.getRestrictionType().equals(RestrictionType.NOT_MEMBER_OF)) {
                            queryString.append(propertyName);
                        }
                    }
                    //if Value is null set default to IS NULL
                    if (restriction.getValue() == null || restriction.getRestrictionType().equals(RestrictionType.NULL)
                            || restriction.getRestrictionType().equals(RestrictionType.NOT_NULL)
                            || restriction.getRestrictionType().equals(RestrictionType.EMPTY)
                            || restriction.getRestrictionType().equals(RestrictionType.NOT_EMPTY)) {
                        //  EQUALS null or IS_NULL
                        if (restriction.getRestrictionType().equals(RestrictionType.EQUALS)) {
                            queryString.append(" IS NULL ");
                        } else if (restriction.getRestrictionType().equals(RestrictionType.NULL) || restriction.getRestrictionType().equals(RestrictionType.EMPTY)) {
                            //if has value and value is false, so negate value 
                            //not NULL == NOT_NULL
                            queryString.append(" IS ");
                            if (restriction.getValue() != null && restriction.getValue() instanceof Boolean && (Boolean) restriction.getValue() == false) {
                                queryString.append("NOT ");
                            }
                            queryString.append(restriction.getRestrictionType().name());
                        } else if (restriction.getRestrictionType().equals(RestrictionType.NOT_NULL) || restriction.getRestrictionType().equals(RestrictionType.NOT_EMPTY)) {
                            //if has value and value is false, so negate value 
                            //not NOT_NULL == NULL
                            queryString.append(" IS ");
                            //negate
                            if (!(restriction.getValue() != null && restriction.getValue() instanceof Boolean && (Boolean) restriction.getValue() == false)) {
                                queryString.append("NOT ");
                            }
                            //remove not (replacing enum name NOT_NULL = NULL)
                            queryString.append(restriction.getRestrictionType().name().replace("NOT_", ""));
                        }
                    } else {
                        //member of has a own logic
                        if (restriction.getRestrictionType().equals(RestrictionType.MEMBER_OF) || restriction.getRestrictionType().equals(RestrictionType.NOT_MEMBER_OF)) {
                            // ?1 member of property
                            queryString.append("?").append(currentParameter);
                            queryString.append(" ").append(restriction.getRestrictionType().getSymbol()).append(" ");
                            queryString.append(propertyName);
                        } else {
                            queryString.append(" ").append(restriction.getRestrictionType().getSymbol()).append(" ");
                            if (restriction.getRestrictionType().equals(RestrictionType.LIKE) || restriction.getRestrictionType().equals(RestrictionType.NOT_LIKE)) {
                                if (restriction.isIlike()) {
                                    queryString.append("UPPER(?").append(currentParameter).append(")");
                                } else {
                                    queryString.append("?").append(currentParameter);
                                }
                            } else if (restriction.getRestrictionType().equals(RestrictionType.IN) || restriction.getRestrictionType().equals(RestrictionType.NOT_IN)) {
                                queryString.append("(?").append(currentParameter).append(")");
                            } else {
                                queryString.append("?").append(currentParameter);
                            }
                        }
                        currentParameter++;
                    }
                }
            }
        }

        return queryString.toString();
    }

    /**
     * @return The complete String of JPQL generated in this QueryBuilder
     */
    public String getQueryString() {

        StringBuilder queryString = new StringBuilder();

        if (type == null) {
            type = QueryType.SELECT;
        }

        if (type.equals(QueryType.SELECT) || type.equals(QueryType.COUNT)) {
            queryString.append(QueryBuilderUtils.getQuerySelectClausule(type, attributes, aggregate));
        } else {
            queryString.append(QueryBuilderUtils.getQuerySelectClausule(type, attribute, aggregate));
        }

        queryString.append("FROM ").append(from.getName()).append(" ");

        if (alias != null) {
            queryString.append(alias).append(" ");
        }

        if (joins != null && joins.size() > 0) {
            queryString.append(joins.getJoinString(type));
        }

        //normalize
        loadNormalizedRestrictions();

        //where clausule
        if (normalizedRestrictions != null && !normalizedRestrictions.isEmpty()) {
            queryString.append(" WHERE ");
        }
        //restrictions
        queryString.append(getQueryStringFromRestrictions(normalizedRestrictions));

        //having clause
        if (normalizedHaving != null && !normalizedHaving.isEmpty()) {
            queryString.append(" HAVING ");
        }
        //restrictions having
        queryString.append(getQueryStringFromRestrictions(normalizedHaving));

        //group by
        if (groupBy != null && !groupBy.trim().isEmpty()) {
            queryString.append(" GROUP BY ").append(groupBy);
        }

        //order by
        if (type.equals(QueryType.SELECT) && orderBy != null && !orderBy.trim().isEmpty()) {
            queryString.append(" ORDER BY ").append(orderBy);
        }

        return queryString.toString();
    }

    public List<QueryParameter> getQueryParameters() {

        List<QueryParameter> queryParameters = new ArrayList<>();

        //WHERE
        queryParameters.addAll(QueryBuilderUtils.getQueryParameters(this.normalizedRestrictions));
        //HAVING
        queryParameters.addAll(QueryBuilderUtils.getQueryParameters(this.normalizedHaving));

        //add Paramters
        if (this.parameters != null && !this.parameters.isEmpty()) {
            queryParameters.addAll(parameters);
        }

        return queryParameters;
    }

    public QueryBuilder selectDistinct(String attributes) {
        this.attributes = "DISTINCT " + attributes;
        return this;
    }

    public QueryBuilder select(String... attributes) {
        if (attributes == null) {
            this.attributes = null;
        } else {
            this.attributes = StringUtils.join(attributes, ", ");
        }
        return this;
    }

    public QueryBuilder aggregate(String... aggregate) {
        if (aggregate == null) {
            this.aggregate = null;
        } else {
            this.aggregate = StringUtils.join(aggregate, ", ");
        }
        return this;
    }

    /**
     * @return Value of clausule "SELECT COUNT(*)"
     */
    public Long count() {
        type = QueryType.COUNT;
        return (Long) createQuery().getSingleResult();
    }

    /**
     * @param attributes
     * @return Value of clausule "SELECT COUNT(attributes)"
     */
    public Long count(String attributes) {
        type = QueryType.COUNT;
        this.attributes = attributes;
        return (Long) createQuery().getSingleResult();
    }

    /**
     * @param attributes
     * @return Value of clausule "SELECT COUNT(attributes)"
     */
    public Long countDistinct(String attributes) {
        type = QueryType.COUNT;
        this.attributes = "DISTINCT " + attributes;
        return (Long) createQuery().getSingleResult();
    }

    /**
     *
     * @param property Property to query
     * @return Value of clausule "SELECT AVG(property)"
     */
    public Number avg(String property) {
        type = QueryType.AVG;
        attribute = property;
        return (Number) createQuery().getSingleResult();
    }

    /**
     *
     * @param property Property to query
     * @param valueWhenNull Return value when query result is null
     * @return Value of clausule "SELECT AVG(property)"
     */
    public Number avg(String property, Number valueWhenNull) {
        Number value = avg(property);
        if (value == null) {
            return valueWhenNull;
        }
        return value;
    }

    /**
     *
     * @param property Property to query
     * @return Value of clausule "SELECT SUM(property)"
     */
    public Number sum(String property) {
        type = QueryType.SUM;
        attribute = property;
        return (Number) createQuery().getSingleResult();
    }

    /**
     *
     * @param property property Property to query
     * @param valueWhenNull Return value when query result is null
     * @return Value of clausule "SELECT SUM(property)"
     */
    public Number sum(String property, Number valueWhenNull) {
        Number number = sum(property);
        if (number == null) {
            return valueWhenNull;
        }
        return number;
    }

    /**
     * @param property Property to query
     * @return Value of clausule "SELECT max(property)"
     */
    public Object max(String property) {
        type = QueryType.MAX;
        attribute = property;
        return createQuery().getSingleResult();
    }

    /**
     * @param property Property to query
     * @param valueWhenNull Return value when query result is null
     * @return Value of clausule "SELECT max(property)"
     */
    public Object max(String property, Object valueWhenNull) {
        Object value = max(property);
        if (value == null) {
            return valueWhenNull;
        }
        return value;
    }

    /**
     *
     * @param property Property to query
     * @return Value of clausule "SELECT min(property)"
     */
    public Object min(String property) {
        type = QueryType.MIN;
        attribute = property;
        return createQuery().getSingleResult();
    }

    /**
     * @param property Property to query
     * @param valueWhenNull Return value when query result is null
     * @return Value of clausule "SELECT min(property)"
     */
    public Object min(String property, Object valueWhenNull) {
        Object value = min(property);
        if (value == null) {
            return valueWhenNull;
        }
        return value;
    }

    /**
     * Creates the JPA Query Object based on current QueryBuilder
     *
     * @return
     */
    public Query createQuery() {

        String queryString = getQueryString();

        if (debug == true) {
            logger.log(Level.INFO, "Query String: {0}", queryString);
            logger.log(Level.INFO, "Parameters: Max Results: {0}, First result: {1}, Order By: {2}, Restrictions: {3}, Joins: {4}", new Object[]{maxResults, firstResult, orderBy, normalizedRestrictions, joins});
        }

        Query query = entityManager.createQuery(queryString);

        List<QueryParameter> parameters = getQueryParameters();
        for (QueryParameter parameter : parameters) {
            //dates (Date and Calendar)
            if (parameter.getTemporalType() != null && (parameter.getValue() instanceof Date || parameter.getValue() instanceof Calendar)) {
                if (parameter.getValue() instanceof Date) {
                    if (parameter.getPosition() != null) {
                        query.setParameter(parameter.getPosition(), (Date) parameter.getValue(), parameter.getTemporalType());
                    } else {
                        if (parameter.getProperty() != null) {
                            query.setParameter(parameter.getProperty(), (Date) parameter.getValue(), parameter.getTemporalType());
                        }
                    }
                } else if (parameter.getValue() instanceof Calendar) {
                    if (parameter.getPosition() != null) {
                        query.setParameter(parameter.getPosition(), (Calendar) parameter.getValue(), parameter.getTemporalType());
                    } else {
                        if (parameter.getProperty() != null) {
                            query.setParameter(parameter.getProperty(), (Calendar) parameter.getValue(), parameter.getTemporalType());
                        }
                    }
                }
            } else {
                if (parameter.getPosition() != null) {
                    query.setParameter(parameter.getPosition(), parameter.getValue());
                } else {
                    if (parameter.getProperty() != null) {
                        query.setParameter(parameter.getProperty(), parameter.getValue());
                    }
                }
            }
        }

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
        return query;
    }

    /**
     * @return entityManager.getSingleResult(), returns null when
     * NoResultExceptionis throw
     */
    public Object getSingleResult() {
        try {
            type = QueryType.SELECT;
            return this.createQuery().getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * @param maxResults max results in Query
     * @return
     */
    public QueryBuilder setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    /**
     *
     * @param firstResult min results in Query
     * @return
     */
    public QueryBuilder setFirstResult(Integer firstResult) {
        this.firstResult = firstResult;
        return this;
    }

    /**
     * @param <T> Result Type
     * @return entityManager.getResultList()
     */
    public <T> List<T> getResultList() {
        return getResultList(null);
    }

    /**
     *
     * @param <T> Result Type
     * @param expectedType The expected type in result
     * @return entityManager.getResultList()
     */
    public <T> List<T> getResultList(Class expectedType) {
        type = QueryType.SELECT;
        List list = this.createQuery().getResultList();
        if (list != null && attributes != null && !attributes.trim().isEmpty() && expectedType != null) {
            return QueryBuilderUtils.getNormalizedResultList(attributes, list, expectedType);
        }
        return list;
    }

    public static Query createNativeQueryFromFile(EntityManager entityManager, String queryPath, Class daoClass) {
        return createNativeQueryFromFile(entityManager, queryPath, daoClass, null);
    }

    public static Query createNativeQueryFromFile(EntityManager entityManager, String queryPath, Class daoClass, Class resultClass) {

        InputStream inputStream = daoClass.getResourceAsStream(queryPath);
        if (inputStream == null) {
            throw new QueryFileNotFoundException("Query File not found: " + queryPath + " in package: " + daoClass.getPackage());
        }
        try {
            String queryString = QueryBuilderUtils.readInputStreamAsString(inputStream);
            if (resultClass != null) {
                return entityManager.createNativeQuery(queryString, resultClass);
            } else {
                return entityManager.createNativeQuery(queryString);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<Restriction> getNormalizedRestrictions() {
        return normalizedRestrictions;
    }

    /**
     * Add a restriction map
     *
     * @param restrictions
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(Map<String, Object> restrictions) {
        if (restrictions != null) {
            for (Map.Entry e : restrictions.entrySet()) {
                this.restrictions.add(new Restriction(e.getKey().toString(), e.getValue()));
            }
        }
        return this;
    }

    /**
     * Add a restriction list
     *
     * @param restrictions
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(List<Restriction> restrictions) {
        if (restrictions != null) {
            this.restrictions.addAll(restrictions);
        }
        return this;
    }

    /**
     * Add a restriction
     *
     * @param restriction
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(Restriction restriction) {
        if (restriction != null) {
            this.restrictions.add(restriction);
        }
        return this;
    }

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, RestrictionType restrictionType) {
        this.add(new Restriction(property, restrictionType));
        return this;
    }

    /**
     * Add a RestrictionType.EQUALS
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, Object value) {
        this.add(new Restriction(property, value));
        return this;
    }

    /**
     * Add a Date restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, RestrictionType restrictionType, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, restrictionType, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, RestrictionType restrictionType, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, restrictionType, value, temporalType));
        return this;
    }

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, RestrictionType restrictionType, Object value) {
        this.add(new Restriction(property, restrictionType, value));
        return this;
    }

    /**
     * Add a restriction
     *
     * @param property
     * @param restrictionType
     * @param value
     * @param likeType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder add(String property, RestrictionType restrictionType, Object value, LikeType likeType) {
        this.add(new Restriction(property, restrictionType, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder addQueryString(String property) {
        this.add(new Restriction(property, RestrictionType.QUERY_STRING));
        return this;
    }

    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @param parameters
     * @return Current Restrictions with added restriction
     */
    public QueryBuilder addQueryString(String property, List<QueryParameter> parameters) {
        Restriction restriction = new Restriction(property, RestrictionType.QUERY_STRING);
        restriction.setParameters(parameters);
        this.add(restriction);
        return this;
    }

    /**
     * Add a RestrictionType.QUERY_STRING
     *
     * @param property
     * @param parameter
     * @return Current Restrictions with added restriction
     */
    public QueryBuilder addQueryString(String property, QueryParameter parameter) {
        Restriction restriction = new Restriction(property, RestrictionType.QUERY_STRING);
        List<QueryParameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        restriction.setParameters(parameters);
        this.add(restriction);
        return this;
    }

    /**
     * Add a RestrictionType.NULL (property 'is null')
     *
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder isNull(String property) {
        this.add(new Restriction(property, RestrictionType.NULL));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_NULL (property 'is not null')
     *
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder isNotNull(String property) {
        this.add(new Restriction(property, RestrictionType.NOT_NULL));
        return this;
    }

    /**
     * Add a RestrictionType.EMPTY (property 'is empty')
     *
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder isEmpty(String property) {
        this.add(new Restriction(property, RestrictionType.EMPTY));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_EMPTY (property 'is not empty')
     *
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder isNotEmpty(String property) {
        this.add(new Restriction(property, RestrictionType.NOT_EMPTY));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder like(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LIKE, value));
        return this;
    }

    /**
     * Add a RestrictionType.MEMBER_OF (value 'member of' property)
     *
     * @param value
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder memberOf(Object value, String property) {
        this.add(new Restriction(property, RestrictionType.MEMBER_OF, value));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_MEMBER_OF (value 'member of' property)
     *
     * @param value
     * @param property
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notMemberOf(Object value, String property) {
        this.add(new Restriction(property, RestrictionType.NOT_MEMBER_OF, value));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @param ilike
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder like(String property, Object value, boolean ilike) {
        this.add(new Restriction(property, RestrictionType.LIKE, value, ilike));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder like(String property, Object value, LikeType likeType) {
        this.add(new Restriction(property, RestrictionType.LIKE, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.LIKE (property 'like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @param ilike
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder like(String property, Object value, LikeType likeType, boolean ilike) {
        this.add(new Restriction(property, RestrictionType.LIKE, value, likeType, ilike));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notLike(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @param ilike
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notLike(String property, Object value, boolean ilike) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value, ilike));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notLike(String property, Object value, LikeType likeType) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value, likeType));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_LIKE (property 'not like' value)
     *
     * @param property
     * @param value
     * @param likeType
     * @param ilike
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notLike(String property, Object value, LikeType likeType, boolean ilike) {
        this.add(new Restriction(property, RestrictionType.NOT_LIKE, value, likeType, ilike));
        return this;
    }

    /**
     * Add a RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.GREATER_THAN (property '>' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterEqualsThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterEqualsThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.GREATER_EQUALS_THAN (property '>=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder greaterEqualsThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder lessThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder lessThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.LESS_THAN (property '&lt' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return
     */
    public QueryBuilder lessThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder lessEqualsThan(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder lessEqualsThan(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.LESS_EQUALS_THAN (property '&lt=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder lessEqualsThan(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.IN (property 'in' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder in(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.IN, value));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_IN (property 'not in' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notIn(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_IN, value));
        return this;
    }

    /**
     * Add a RestrictionType.EQUALS (property '=' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder equals(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.EQUALS (property '=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder equals(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.EQUALS (property '=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder equals(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.NOT_EQUALS (property '!=' value)
     *
     * @param property
     * @param value
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notEquals(String property, Object value) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value));
        return this;
    }

    /**
     * Add a Date RestrictionType.NOT_EQUALS (property '!=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notEquals(String property, Date value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a Calendar RestrictionType.NOT_EQUALS (property '!=' value)
     *
     * @param property
     * @param value
     * @param temporalType
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder notEquals(String property, Calendar value, TemporalType temporalType) {
        this.add(new Restriction(property, RestrictionType.NOT_EQUALS, value, temporalType));
        return this;
    }

    /**
     * Add a RestrictionType.OR
     *
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder or() {
        this.add(new Restriction(RestrictionType.OR));
        return this;
    }

    /**
     * Add a RestrictionType.START_GROUP
     *
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder startGroup() {
        this.add(new Restriction(RestrictionType.START_GROUP));
        return this;
    }

    /**
     * Add a RestrictionType.END_GROUP
     *
     * @return Current QueryBuilder with added restriction
     */
    public QueryBuilder endGroup() {
        this.add(new Restriction(RestrictionType.END_GROUP));
        return this;
    }

    public QueryBuilder addParameter(QueryParameter parameter) {
        if (parameter != null) {
            this.parameters.add(parameter);
        }
        return this;
    }

    public QueryBuilder addParameters(List<QueryParameter> parameters) {
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
        return this;
    }

    public QueryBuilder join(JoinBuilder joinBuilder) {
        if (joinBuilder != null) {
            joins.addAll(joinBuilder);
        }
        return this;
    }

    public QueryBuilder leftJoin(String join) {
        joins.leftJoin(join);
        return this;
    }

    public QueryBuilder leftJoin(String join, String alias) {
        joins.leftJoin(join, alias);
        return this;
    }

    public QueryBuilder leftJoinFetch(String join) {
        joins.leftJoinFetch(join);
        return this;
    }

    public QueryBuilder leftJoinFetch(String join, String alias) {
        joins.leftJoinFetch(join, alias);
        return this;
    }

    public QueryBuilder innerJoin(String join) {
        joins.innerJoin(join);
        return this;
    }

    public QueryBuilder innerJoin(String join, String alias) {
        joins.innerJoin(join, alias);
        return this;
    }

    public QueryBuilder innerJoinFetch(String join) {
        joins.innerJoinFetch(join);
        return this;
    }

    public QueryBuilder innerJoinFetch(String join, String alias) {
        joins.innerJoinFetch(join, alias);
        return this;
    }

    public QueryBuilder join(String join) {
        joins.join(join);
        return this;
    }

    public QueryBuilder join(String join, String alias) {
        joins.join(join, alias);
        return this;
    }

    public QueryBuilder joinFetch(String join) {
        joins.join(join);
        return this;
    }

    public QueryBuilder joinFetch(String join, String alias) {
        joins.joinFetch(join, alias);
        return this;
    }

    public QueryBuilder rightJoin(String join) {
        joins.rightJoin(join);
        return this;
    }

    public QueryBuilder rightJoin(String join, String alias) {
        joins.rightJoin(join, alias);
        return this;
    }

    public QueryBuilder rightJoinFetch(String join) {
        joins.rightJoinFetch(join);
        return this;
    }

    public QueryBuilder rightJoinFetch(String join, String alias) {
        joins.rightJoinFetch(join, alias);
        return this;
    }

}
