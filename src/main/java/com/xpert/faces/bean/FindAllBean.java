package com.xpert.faces.bean;

import com.xpert.DAO;
import com.xpert.faces.utils.ValueExpressionAnalyzer;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.utils.ReflectionUtils;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.utils.EntityUtils;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import java.io.Serializable;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Generic bean to get objects, if is enum get values from enum, if is a entity
 * try to select from data base. This bean works better on ViewScope to evict
 * duplicates query.
 *
 * @author ayslan
 */
public abstract class FindAllBean implements Serializable {

    private static final long serialVersionUID = -6524834779133530104L;
    
    private static final Logger logger = Logger.getLogger(FindAllBean.class.getName());

    /**
     * Define de default order for the Map Key (Class), also set the default
     * attribute to itemLabel in SelectItem
     *
     * @return
     */
    public abstract Map<Class, ClassModel> getClassModel();
    private final Map<Class, List> values = new HashMap<>();
    private boolean reload = false;
    private BaseDAO baseDAO;

    @PostConstruct
    public void init() {
        if (getBaseDAO() == null) {
            baseDAO = new DAO();
        }
    }

    public BaseDAO getBaseDAO() {
        return baseDAO;
    }

    /**
     *
     * @param object
     * @return
     */
    public List get(Object object) {
        if (object instanceof Class) {
            return getFromClass((Class) object);
        } else if (object instanceof UIComponent) {
            return getFromComponent((UIComponent) object);
        }
        return null;
    }

    public List get(Class clazz, String order) {

        List objects = values.get(clazz);
        if (objects == null || objects.isEmpty() || reload) {
            List<Restriction> restrictions = null;
            ClassModel classModel = getClassModel().get(clazz);
            if (classModel != null) {
                restrictions = classModel.getRestrictions();
            }
            objects = getBaseDAO().list(clazz, restrictions, order);
            values.put(clazz, objects);
        }

        return objects;
    }

    /**
     * Get a type from UIComponent. If component is a primefaces Column, try to
     * get "filterBy", other components get "value"
     *
     * @param component
     * @return
     */
    public Class getType(UIComponent component) {
        Class type = null;
        ValueExpression valueExpression = component.getValueExpression("value");
        ValueExpressionAnalyzer expressionAnalyzer = new ValueExpressionAnalyzer(valueExpression);
        ValueReference valueReference = expressionAnalyzer.getReference(FacesContext.getCurrentInstance().getELContext());
        String property = valueReference.getProperty().toString();
        Class clazz = valueReference.getBase().getClass();
        try {
            Field field = ReflectionUtils.getDeclaredField(clazz, property);
            if (field != null) {
                type = field.getType();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return type;
    }

    public List getFromComponent(UIComponent component) {
        long begin = System.currentTimeMillis();
        Class type = getType(component);
        if (type == null) {
            logger.log(Level.SEVERE, "Type no found in findAllBean.getFromComponent for component {0}", component.getClientId());
            return null;
        }

        logger.log(Level.INFO, "Type found: {0}", type);

        List list = get(type);
        long end = System.currentTimeMillis();
        logger.log(Level.INFO, "Time in miliseconds: {0}", (end - begin));

        return list;
    }

    public List getFromClass(Class clazz) {
        if (clazz.isEnum()) {
            return Arrays.asList(clazz.getEnumConstants());
        }
        if (getClassModel() != null) {
            ClassModel classModel = getClassModel().get(clazz);
            if (classModel != null) {
                return get(clazz, classModel.getOrder());
            } else {
                return get(clazz, null);
            }
        } else {
            return get(clazz, null);
        }
    }

    public SelectItem[] getSelect(Class clazz) {
        return getSelect(clazz, true);
    }

    public SelectItem[] getSelect(Class clazz, boolean emptyOption) {

        List objects = get(clazz);
        ClassModel classModel = getClassModel().get(clazz);
        SelectItem[] options = new SelectItem[objects.size() + (emptyOption ? 1 : 0)];

        Integer count = 0;
        boolean isEnum = clazz.isEnum();
        try {
            if (emptyOption) {
                options[count] = new SelectItem("", "");
                count++;
            }
            for (Object bean : objects) {
                String itemLabel = getItemLabel(classModel, bean);
                if (!isEnum) {
                    Object id = PropertyUtils.getProperty(bean, EntityUtils.getIdFieldName(clazz));
                    options[count] = new SelectItem(id, itemLabel);
                } else {
                    options[count] = new SelectItem(((Enum) bean).name(), itemLabel);
                }

                count++;
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error converting class: {0}: {1}", new Object[]{clazz, ex.getMessage()});
        }
        return options;
    }

    private String getItemLabel(ClassModel classModel, Object bean) throws Exception {
        if (classModel == null) {
            return bean.toString();
        }
        //ClassModel itemLabel null then use order
        if (classModel.getItemLabel() != null && !classModel.getItemLabel().isEmpty()) {
            //try to separate with comma
            StringBuilder builder = new StringBuilder();
            for (String label : classModel.getItemLabel().split(",")) {
                if (label != null && !label.isEmpty()) {
                    if (builder.length() > 0) {
                        builder.append(" - ");
                    }
                    builder.append((String) PropertyUtils.getProperty(bean, label.trim()));
                }
            }
            return builder.toString();
        } else if (classModel.getOrder() != null && !classModel.getOrder().isEmpty()) {
            return (String) PropertyUtils.getProperty(bean, classModel.getOrder());
        } else {
            return bean.toString();
        }
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
}
