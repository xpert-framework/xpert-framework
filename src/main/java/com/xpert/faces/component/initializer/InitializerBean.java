package com.xpert.faces.component.initializer;

import com.xpert.DAO;
import com.xpert.faces.utils.FacesUtils;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.persistence.EntityManager;
import org.hibernate.LazyInitializationException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 *
 * @author Ayslan
 */
public class InitializerBean {

    private static final boolean DEBUG = false;
    private static final Logger logger = Logger.getLogger(InitializerBean.class.getName());
    private Map<ClassIdentifier, Object> cache = new HashMap<ClassIdentifier, Object>();
    private DAO dao;
    private EntityManager entityManager;

    public InitializerBean(EntityManager entityManager) {
        this.entityManager = entityManager;
        try {
            this.dao = new DAO();
            if (this.entityManager != null) {
                this.dao.setEntityManager(this.entityManager);
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error on InitilizerBean Cosntructor.", t);
        }
    }

    public InitializerBean() {
        try {
            dao = new DAO();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error on InitilizerBean Cosntructor.", t);
        }
    }

    public void init() {
    }

    public void initialize(ComponentSystemEvent event) {
        try {
            initialize(event.getComponent(), FacesContext.getCurrentInstance(), (String) null);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, null, t);
        }
    }

    public String getParentExpression(String expression) {

        if (expression != null && !expression.isEmpty()) {
            int index = expression.lastIndexOf(".");
            if (index > -1) {
                return expression.substring(0, index) + "}";
            }
        }

        return expression;
    }

    public void initializeValue(String expression) {

        Object value = null;

        try {
            value = FacesUtils.getBeanByEl(expression);
        } catch (ELException ex) {
            if (ex.getCause() != null && ex.getCause() instanceof LazyInitializationException) {
                //try to initializer parent expression
                initializeValue(getParentExpression(expression));
                //get expression now initialized
                value = FacesUtils.getBeanByEl(expression);
            }
        }

        LazyInitializer lazyInitializer = null;

        if (value instanceof HibernateProxy) {
            lazyInitializer = ((HibernateProxy) value).getHibernateLazyInitializer();
            Object cached = cache.get(new ClassIdentifier(lazyInitializer.getIdentifier(), lazyInitializer.getPersistentClass()));
            if (cached != null) {
                if (DEBUG) {
                    logger.log(Level.INFO, "Initializer: Object from class {0} id {1} expression found in cache", new Object[]{lazyInitializer.getPersistentClass().getName(), lazyInitializer.getIdentifier(), expression});
                }
                FacesUtils.setValueEl(expression, cached);
                return;
            }

        }

        if (value instanceof HibernateProxy || value instanceof PersistentCollection) {
            if (DEBUG) {
                logger.log(Level.INFO, "Initializer: Initializing expression {0} in database", new Object[]{expression});
            }
            Object initialized = dao.getInitialized(value);
            if (value instanceof HibernateProxy) {
                cache.put(new ClassIdentifier(lazyInitializer.getIdentifier(), lazyInitializer.getPersistentClass()), initialized);
            }
            FacesUtils.setValueEl(expression, initialized);
        }
    }

    /**
     *
     * @param component Component to get ValueExpression
     * @param context Current FacesCOntext
     * @param property Property Name to be initialized
     */
    public void initialize(UIComponent component, FacesContext context, String property) {
        if (property == null || property.isEmpty()) {
            property = "value";
        }
        ValueExpression valueExpression = component.getValueExpression(property);
        initialize(component, context, valueExpression);
    }

    /**
     *
     * @param component Component to get ValueExpression
     * @param context Current FacesContext
     * @param valueExpression If passed ValueExpression will not be used, this value will
     * be initilized
     */
    public void initialize(UIComponent component, FacesContext context, ValueExpression valueExpression) {
        if (valueExpression != null) {
            String expression = valueExpression.getExpressionString();
            Matcher matcher = Pattern.compile("\\#\\{.*?\\}").matcher(expression);
            while (matcher.find()) {
                initializeValue(matcher.group());
            }
        }
    }

    public class ClassIdentifier {

        private Object id;
        private Class entity;

        public ClassIdentifier(Object id, Class entity) {
            this.id = id;
            this.entity = entity;
        }

        public Class getEntity() {
            return entity;
        }

        public void setEntity(Class entity) {
            this.entity = entity;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ClassIdentifier other = (ClassIdentifier) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            if (this.entity != other.entity && (this.entity == null || !this.entity.equals(other.entity))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
            hash = 97 * hash + (this.entity != null ? this.entity.hashCode() : 0);
            return hash;
        }
    }
}
