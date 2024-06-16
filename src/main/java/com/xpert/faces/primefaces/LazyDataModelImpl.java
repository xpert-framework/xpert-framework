package com.xpert.faces.primefaces;

import com.xpert.faces.bean.Xpert;
import com.xpert.faces.component.restorablefilter.RestorableFilter;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.query.JoinBuilder;
import com.xpert.persistence.query.LikeType;
import com.xpert.persistence.query.QueryBuilder;
import com.xpert.persistence.query.QueryParameter;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import com.xpert.persistence.query.Restrictions;
import com.xpert.persistence.utils.EntityUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 * A JPA based implementation os primefaces LazyDataModel
 *
 * @author ayslan
 * @param <T>
 */
public class LazyDataModelImpl<T> extends LazyDataModel {

    private static final long serialVersionUID = 5508266758681517868L;

    private boolean debug = true;
    private static final Logger logger = Logger.getLogger(LazyDataModelImpl.class.getName());

    private BaseDAO<T> dao;
    private String defaultOrder;
    private String currentOrderBy;
    private String attributes;
    private OrderByHandler orderByHandler;
    private FilterByHandler filterByHandler;
    private LazyCountType lazyCountType;
    private Integer currentRowCount;
    /*
     * to add restrictions on query to filter table
     */
    private List<Restriction> restrictions;
    private List<Restriction> queryRestrictions;
    private Restriction restriction;
    private JoinBuilder joinBuilder;
    private boolean loadData = true;
    private boolean restorableFilter = false;
    private Map currentFilters;
    private List<QueryParameter> parameters = new ArrayList<>();
    private boolean auditQuery = false;

    /**
     * @param attributes Attributes of object thet will be loaded
     * @param defaultOrder The default data model "Order By"
     * @param restriction A restriction to be added in query
     * @param dao An instance of BaseDAO
     */
    public LazyDataModelImpl(String attributes, String defaultOrder, Restriction restriction, BaseDAO<T> dao) {
        this.dao = dao;
        this.attributes = attributes;
        this.defaultOrder = defaultOrder;
        this.restriction = restriction;
    }

    /**
     * @param attributes Attributes of object thet will be loaded
     * @param defaultOrder The default data model "Order By"
     * @param restrictions Restrictions to be added in query
     * @param dao An instance of BaseDAO
     */
    public LazyDataModelImpl(String attributes, String defaultOrder, List<Restriction> restrictions, BaseDAO<T> dao) {
        this.dao = dao;
        this.attributes = attributes;
        this.defaultOrder = defaultOrder;
        this.restrictions = restrictions;
    }

    /**
     * @param defaultOrder The default data model "Order By"
     * @param restriction Restrictions to be added in query
     * @param dao An instance of BaseDAO
     */
    public LazyDataModelImpl(String defaultOrder, Restriction restriction, BaseDAO<T> dao) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
        this.restriction = restriction;
    }

    /**
     *
     * @param defaultOrder The default data model "Order By"
     * @param restrictions Restrictions to be added in query
     * @param dao An instance of BaseDAO
     */
    public LazyDataModelImpl(String defaultOrder, List<Restriction> restrictions, BaseDAO<T> dao) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
        this.restrictions = restrictions;
    }

    /**
     * @param defaultOrder The default data model "Order By"
     * @param dao An instance of BaseDAO
     */
    public LazyDataModelImpl(String defaultOrder, BaseDAO<T> dao) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
    }

    /**
     *
     * @param defaultOrder The default data model "Order By"
     * @param restriction A restriction to be added in query
     * @param dao An instance of BaseDAO
     * @param joinBuilder Joins to be added in Query
     */
    public LazyDataModelImpl(String defaultOrder, Restriction restriction, BaseDAO<T> dao, JoinBuilder joinBuilder) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
        this.restriction = restriction;
        this.joinBuilder = joinBuilder;
    }

    /**
     *
     * @param defaultOrder The default data model "Order By"
     * @param restrictions Restrictions to be added in query
     * @param dao An instance of BaseDAO
     * @param joinBuilder Joins to be added in Query
     */
    public LazyDataModelImpl(String defaultOrder, List<Restriction> restrictions, BaseDAO<T> dao, JoinBuilder joinBuilder) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
        this.restrictions = restrictions;
        this.joinBuilder = joinBuilder;
    }

    /**
     *
     * @param defaultOrder The default data model "Order By"
     * @param dao An instance of BaseDAO
     * @param joinBuilder Joins to be added in Query
     */
    public LazyDataModelImpl(String defaultOrder, BaseDAO<T> dao, JoinBuilder joinBuilder) {
        this.dao = dao;
        this.defaultOrder = defaultOrder;
        this.joinBuilder = joinBuilder;
    }

    /**
     * @param orderBy String from primefaces
     * @param order Order from primefaces
     * @return The "Order By" to the data model
     */
    private String getOrderBy(String orderBy, SortOrder order) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            orderBy = defaultOrder;
        } else {
            OrderByHandler orderHandler = getOrderByHandler();
            String orderByFromHandler = null;
            if (orderHandler != null) {
                orderByFromHandler = orderHandler.getOrderBy(orderBy);
            }
            if (orderHandler != null && orderByFromHandler != null && !orderByFromHandler.isEmpty()) {
                orderBy = orderByFromHandler;
            } else {
                if (joinBuilder != null && joinBuilder.getRootAlias() != null) {
                    orderBy = joinBuilder.getRootAlias() + "." + orderBy;
                }
            }
            if (order.equals(SortOrder.DESCENDING)) {
                orderBy = orderBy + " DESC";
            }
        }

        return orderBy;
    }

    /**
     * @param filters Filters from "load" method
     * @return The filter map converted into "restrictions"
     */
    private List<Restriction> getRestrictionsFromFilterMap(Map<String, Object> filters) {
        List<Restriction> filterRestrictions = new ArrayList<>();

        if (filters == null || filters.isEmpty()) {
            return filterRestrictions;
        }

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            Object filterValue = entry.getValue();
            if (filterValue == null) {
                continue;
            }

            FilterMeta filterMeta = (FilterMeta) filterValue;
            addRestrictions(filterRestrictions, filterMeta);
        }

        return filterRestrictions;
    }

    private void addRestrictions(List<Restriction> filteres, FilterMeta filterMeta) {

        FilterByHandler filterHandler = getFilterByHandler();
        if (filterHandler != null) {
            Restrictions r = filterHandler.getFilterBy(filterMeta.getField(), filterMeta.getFilterValue().toString());
            if (r != null && !r.isEmpty()) {
                filteres.addAll(r);
                return;
            }
        }

        logDebug(filterMeta);

        String property = getProperty(filterMeta);
        Object filterValue = filterMeta.getFilterValue();

        if (filterValue == null) {
            return;
        }

        switch (filterMeta.getMatchMode()) {
            case STARTS_WITH:
                filteres.add(new Restriction(property, RestrictionType.LIKE, filterValue.toString(), LikeType.BEGIN));
                break;
            case NOT_STARTS_WITH:
                filteres.add(new Restriction(property, RestrictionType.NOT_LIKE, filterValue.toString(), LikeType.BEGIN));
                break;
            case ENDS_WITH:
                filteres.add(new Restriction(property, RestrictionType.LIKE, filterValue.toString(), LikeType.END));
                break;
            case NOT_ENDS_WITH:
                filteres.add(new Restriction(property, RestrictionType.NOT_LIKE, filterValue.toString(), LikeType.END));
                break;
            case CONTAINS:
                filteres.add(new Restriction(property, RestrictionType.LIKE, filterValue.toString(), LikeType.BOTH));
                break;
            case NOT_CONTAINS:
                filteres.add(new Restriction(property, RestrictionType.NOT_LIKE, filterValue.toString(), LikeType.BOTH));
                break;
            case EXACT:
            case EQUALS:
                filteres.add(new Restriction(property, RestrictionType.EQUALS, filterValue.toString()));
                break;
            case NOT_EXACT:
            case NOT_EQUALS:
                filteres.add(new Restriction(property, RestrictionType.NOT_EQUALS, filterValue.toString()));
                break;
            case LESS_THAN:
                filteres.add(new Restriction(property, RestrictionType.LESS_THAN, filterValue.toString()));
                break;
            case LESS_THAN_EQUALS:
                filteres.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, filterValue.toString()));
                break;
            case GREATER_THAN:
                filteres.add(new Restriction(property, RestrictionType.GREATER_THAN, filterValue.toString()));
                break;
            case GREATER_THAN_EQUALS:
                filteres.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, filterValue.toString()));
                break;
            case IN:
                filteres.add(new Restriction(property, RestrictionType.IN, filterArrayToList(filterValue)));
                break;
            case NOT_IN:
                filteres.add(new Restriction(property, RestrictionType.NOT_IN, filterArrayToList(filterValue)));
                break;
            case BETWEEN:
                List list = filterArrayToList(filterValue);
                filteres.add(new Restriction(property, RestrictionType.GREATER_EQUALS_THAN, list.get(0)));
                filteres.add(new Restriction(property, RestrictionType.LESS_EQUALS_THAN, list.get(1)));
                break;
            case GLOBAL:
                throw new UnsupportedOperationException("MatchMode.GLOBAL currently not supported!");
        }

    }

    private void logDebug(FilterMeta filterMeta) {
        if (debug) {
            logger.log(Level.INFO, "Restriction added. Name: {0}, Value: {1}", new Object[]{filterMeta.getField(), filterMeta.getFilterValue().toString()});
        }
    }

    private String getProperty(FilterMeta filterMeta) {
        String property = filterMeta.getField();
        if (joinBuilder != null && joinBuilder.getRootAlias() != null) {
            property = joinBuilder.getRootAlias() + "." + property;
        }
        return property;
    }

    private ArrayList<Object> filterArrayToList(Object filterValue) {
        if (filterValue instanceof Collection<?> collection) {
            return new ArrayList<>(collection);
        } else if (filterValue != null && filterValue.getClass().isArray()) {
            return new ArrayList<>(Arrays.asList(filterValue));
        } else {
            throw new IllegalArgumentException("O objeto deve ser uma Collection ou um array.");
        }
    }

    @Override
    public int count(Map filters) {

        if (isRestorableFilter()) {
            RestorableFilter.restoreFilterFromSession(filters);
        }

        this.currentFilters = filters;

        List<Restriction> currentQueryRestrictions = getCurrentQueryRestrictions();

        boolean restrictionsChanged = !currentQueryRestrictions.equals(queryRestrictions);

        this.queryRestrictions = currentQueryRestrictions;

        LazyCountType countType = getLazyCountType();
        if (countType == null) {
            countType = LazyCountType.ALWAYS;
        }

        // If ALWAYS or (ONLY_ONCE and not set currentRowCount or restrictions has changed)
        if (countType.equals(LazyCountType.ALWAYS)
                || (countType.equals(LazyCountType.ONLY_ONCE) && (currentRowCount == null || restrictionsChanged))) {

            QueryBuilder queryBuilderCount = buildQueryBuilder();
            // added distinct verification
            if (joinBuilder != null && joinBuilder.isDistinct()) {
                currentRowCount = queryBuilderCount.countDistinct(joinBuilder.getRootAlias()).intValue();
            } else {
                currentRowCount = queryBuilderCount.count().intValue();
            }
            if (debug) {
                logger.log(Level.INFO, "Count on entity {0}, records found: {1} ", new Object[]{dao.getEntityClass().getName(), currentRowCount});
            }
            this.setRowCount(currentRowCount);
        }
        if (countType.equals(LazyCountType.ONLY_ONCE)) {
            this.setRowCount(currentRowCount);
        } else if (countType.equals(LazyCountType.NONE)) {
//            currentRowCount = dados.size();
            this.setRowCount(Integer.MAX_VALUE);
        }

        return currentRowCount;
    }

    @Override
    public List load(int first, int pageSize, Map sortBy, Map filters) {

        Map<String, SortMeta> sortByMap = sortBy;

        if (!isLoadData()) {
            setRowCount(0);
            return null;
        }

        if (isRestorableFilter()) {
            RestorableFilter.restoreFilterFromSession(filters);
        }

        this.currentFilters = filters;
        long begin = System.currentTimeMillis();

        LazyCountType countType = getLazyCountType();
        if (countType == null) {
            countType = LazyCountType.ALWAYS;
        }

        String orderByField = "";
        if (sortBy != null) {
            StringJoiner orderByJoiner = new StringJoiner(", ");
            for (Entry<String, SortMeta> entry : sortByMap.entrySet()) {
                orderByJoiner.add(getOrderBy(entry.getValue().getField(), entry.getValue().getOrder()));
            }
            orderByField = orderByJoiner.toString();
        }

        if (debug) {
            logger.log(Level.INFO, "Lazy Count Type: {0}. Using order by {1}", new Object[]{countType, orderByField});
        }

        List<Restriction> currentQueryRestrictions = getCurrentQueryRestrictions();

        this.currentOrderBy = orderByField;

        String select = null;
        if (attributes != null && !attributes.isEmpty()) {
            select = attributes;
        } else if (joinBuilder != null && joinBuilder.getRootAlias() != null && !joinBuilder.getRootAlias().isEmpty()) {
            select = joinBuilder.getRootAlias();
        }

        //update current restrictions
        queryRestrictions = currentQueryRestrictions;

        //added distinct verification
        QueryBuilder queryBuilder = buildQueryBuilder();
        if (joinBuilder != null && joinBuilder.isDistinct()) {
            queryBuilder.selectDistinct(select);
        } else {
            queryBuilder.select(select);
        }

        List<T> dados = queryBuilder
                .addParameters(parameters)
                .orderBy(orderByField)
                .setFirstResult(first)
                .setMaxResults(pageSize)
                .getResultList();

        if (debug) {
            logger.log(Level.INFO, "Select on entity {0}, records found: {1} ", new Object[]{dao.getEntityClass().getName(), dados.size()});
        }

        if (debug) {
            long end = System.currentTimeMillis();
            logger.log(Level.INFO, "Load method executed in {0} milliseconds", (end - begin));
        }
        if (isRestorableFilter()) {
            RestorableFilter.storeFilterInSession(filters);
        }

        return dados;
    }

    private List<Restriction> getCurrentQueryRestrictions() {
        List<Restriction> currentQueryRestrictions = new ArrayList<>();

        if (restrictions != null && !restrictions.isEmpty()) {
            currentQueryRestrictions.addAll(restrictions);
        }
        if (restriction != null) {
            currentQueryRestrictions.add(restriction);
        }
        //restrictions from filter
        if (currentFilters != null && !currentFilters.isEmpty()) {
            currentQueryRestrictions.addAll(getRestrictionsFromFilterMap(currentFilters));
        }
        return currentQueryRestrictions;
    }

    @Override
    public Object getRowData(String rowKey) {
        if (rowKey != null && !rowKey.isEmpty()) {
            //convert id (id can be integer, long, string, etc...)
            Object id = EntityUtils.getIdFromString(rowKey, getDao().getEntityClass());
            if (id != null) {
                return getDao().find(id);
            }
        }
        return null;
    }

    /**
     * return the query builder of current restrictions and join builder
     *
     * @return
     */
    private QueryBuilder buildQueryBuilder() {
        return dao.getQueryBuilder()
                .from(dao.getEntityClass(), (joinBuilder != null ? joinBuilder.getRootAlias() : null))
                .join(joinBuilder)
                .add(queryRestrictions)
                .debug(debug)
                .audit(isAuditQuery());
    }

    /**
     * Return a sum of field, this method gets the QueryBuilder and add the
     * current restrictions to generate the query
     *
     * @param field
     * @return
     */
    public Object sum(String field) {
        return buildQueryBuilder().sum(field);
    }

    /**
     * Return a average of field, this method gets the QueryBuilder and add the
     * current restrictions to generate the query
     *
     * @param field
     * @return
     */
    public Object avg(String field) {
        return buildQueryBuilder().avg(field);
    }

    /**
     * Return a min value of field, this method gets the QueryBuilder and add
     * the current restrictions to generate the query
     *
     * @param field
     * @return
     */
    public Object min(String field) {
        return buildQueryBuilder().min(field);
    }

    /**
     * Return a max value of field, this method gets the QueryBuilder and add
     * the current restrictions to generate the query
     *
     * @param field
     * @return
     */
    public Object max(String field) {
        return buildQueryBuilder().max(field);
    }

    /**
     * Return Paginator Template
     *
     * @return
     */
    public String getPaginatorTemplate() {
        if (isLazyCountTypeNone()) {
            return Xpert.UNKNOW_COUNT_PAGINATOR_TEMPLATE;
        }
        return Xpert.DEFAULT_PAGINATOR_TEMPLATE;
    }

    private boolean isLazyCountTypeNone() {
        LazyCountType countType = getLazyCountType();
        return countType != null && countType.equals(LazyCountType.NONE);
    }

    /**
     *
     * @return Current Page Report Template
     */
    public String getCurrentPageReportTemplate() {
        if (isLazyCountTypeNone()) {
            return new Xpert().getUnknowCountCurrentPageReportTemplate();
        }
        return new Xpert().getDefaultCurrentPageReportTemplate();
    }

    /**
     * Return the count objects from data base, based on filters from data table
     *
     * @return
     */
    public Long getCountAllResults() {

        //create a querybuilder for count
        QueryBuilder queryBuilderCount = dao.getQueryBuilder()
                .from(dao.getEntityClass(), (joinBuilder != null ? joinBuilder.getRootAlias() : null))
                .join(joinBuilder)
                .add(getCurrentQueryRestrictions())
                .debug(debug);

        Long rowCount;
        //added distinct verification
        if (joinBuilder != null && joinBuilder.isDistinct()) {
            rowCount = queryBuilderCount.countDistinct(joinBuilder.getRootAlias());
        } else {
            rowCount = queryBuilderCount.count();
        }

        return rowCount;
    }

    /**
     * Return all objects from data base, based on filters from data table
     *
     * @param orderBy
     * @return
     */
    public List getAllResults(String orderBy) {
        return dao.getQueryBuilder()
                .from(dao.getEntityClass(), (joinBuilder != null ? joinBuilder.getRootAlias() : null))
                .select(attributes)
                .add(getCurrentQueryRestrictions())
                .addParameters(parameters)
                .join(joinBuilder)
                .orderBy(orderBy)
                .debug(debug)
                .audit(isAuditQuery())
                .getResultList();
    }

    /**
     * @return all objects from data base, based on filters from data table
     */
    public List getAllResults() {
        return getAllResults(currentOrderBy);
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (getPageSize() == 0) {
            setPageSize(1);
        }
        super.setRowIndex(rowIndex);
    }

    public void addParameter(QueryParameter parameter) {
        if (parameter != null) {
            this.parameters.add(parameter);
        }
    }

    public void addParameters(List<QueryParameter> parameters) {
        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
    }

    public List<QueryParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<QueryParameter> parameters) {
        this.parameters = parameters;
    }

    public BaseDAO<T> getDao() {
        return dao;
    }

    public void setDao(BaseDAO<T> dao) {
        this.dao = dao;
    }

    /**
     * Indicates if data will be loaded
     *
     * @return
     */
    public boolean isLoadData() {
        return loadData;
    }

    public void setLoadData(boolean loadData) {
        this.loadData = loadData;
    }

    /**
     * Default order by of query
     *
     * @return
     */
    public String getDefaultOrder() {
        return defaultOrder;
    }

    public void setDefaultOrder(String defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    /**
     * Restrictions to be added in Query
     *
     * @return
     */
    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public OrderByHandler getOrderByHandler() {
        return orderByHandler;
    }

    public void setOrderByHandler(OrderByHandler orderByHandler) {
        this.orderByHandler = orderByHandler;
    }

    public String getCurrentOrderBy() {
        return currentOrderBy;
    }

    public void setCurrentOrderBy(String currentOrderBy) {
        this.currentOrderBy = currentOrderBy;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    /**
     *
     * @return Current Restrictions (Data Table restrictions and defined
     * Restrictions)
     */
    public List<Restriction> getQueryRestrictions() {
        return queryRestrictions;
    }

    public void setQueryRestrictions(List<Restriction> queryRestrictions) {
        this.queryRestrictions = queryRestrictions;
    }

    public JoinBuilder getJoinBuilder() {
        return joinBuilder;
    }

    public void setJoinBuilder(JoinBuilder joinBuilder) {
        this.joinBuilder = joinBuilder;
    }

    public LazyCountType getLazyCountType() {
        return lazyCountType;
    }

    public void setLazyCountType(LazyCountType lazyCountType) {
        this.lazyCountType = lazyCountType;
    }

    public Integer getCurrentRowCount() {
        return currentRowCount;
    }

    public void setCurrentRowCount(Integer currentRowCount) {
        this.currentRowCount = currentRowCount;
    }

    public FilterByHandler getFilterByHandler() {
        return filterByHandler;
    }

    public void setFilterByHandler(FilterByHandler filterByHandler) {
        this.filterByHandler = filterByHandler;
    }

    public boolean isRestorableFilter() {
        return restorableFilter;
    }

    public void setRestorableFilter(boolean restorableFilter) {
        this.restorableFilter = restorableFilter;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isAuditQuery() {
        return auditQuery;
    }

    public void setAuditQuery(boolean auditQuery) {
        this.auditQuery = auditQuery;
    }

}
