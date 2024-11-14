package com.xpert.maker;

import com.xpert.audit.model.AbstractAuditing;
import com.xpert.audit.model.AbstractMetadata;
import com.xpert.persistence.utils.EntityUtils;
import freemarker.template.TemplateException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ayslan
 */
public class PersistenceMappedBean implements Serializable {

    private static final long serialVersionUID = 3106507930332270388L;
    private static final Logger logger = Logger.getLogger(PersistenceMappedBean.class.getName());
    private EntityManager entityManager;

    public PersistenceMappedBean(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<MappedBean> getMappedBeans(List<Class> classes, BeanConfiguration beanConfiguration) {
        List<MappedBean> mappedBeans = new ArrayList<>();
        if (beanConfiguration != null) {
            if (beanConfiguration.getManagedBeanSuffix() == null || beanConfiguration.getManagedBeanSuffix().isEmpty()) {
                beanConfiguration.setManagedBeanSuffix(BeanCreator.SUFFIX_MANAGED_BEAN);
            }
            if (beanConfiguration.getBusinessObjectSuffix() == null || beanConfiguration.getBusinessObjectSuffix().isEmpty()) {
                beanConfiguration.setBusinessObjectSuffix(BeanCreator.SUFFIX_BUSINESS_OBJECT);
            }
            if (beanConfiguration.getDatePattern() == null || beanConfiguration.getDatePattern().isEmpty()) {
                beanConfiguration.setDatePattern(BeanCreator.DEFAULT_DATE_PATTERN);
            }
            if (beanConfiguration.getTimePattern() == null || beanConfiguration.getTimePattern().isEmpty()) {
                beanConfiguration.setTimePattern(BeanCreator.DEFAULT_TIME_PATTERN);
            }
        }
        for (Class clazz : classes) {
            //ignore Embeddable
            if (!clazz.isAnnotationPresent(Embeddable.class)) {
                try {
                    MappedBean mappedBean = new MappedBean(clazz);
                    mappedBean.setI18n(BeanCreator.getI18N(clazz));
                    mappedBean.setManagedBean(BeanCreator.createBean(new Bean(clazz, BeanType.MANAGED_BEAN), beanConfiguration));
                    mappedBean.setBusinnesObject(BeanCreator.createBean(new Bean(clazz, BeanType.BUSINESS_OBJECT), beanConfiguration));
                    mappedBean.setDao(BeanCreator.createBean(new Bean(clazz, BeanType.DAO), beanConfiguration));
                    mappedBean.setDaoImpl(BeanCreator.createBean(new Bean(clazz, BeanType.DAO_IMPL), beanConfiguration));
                    mappedBean.setFormCreateView(BeanCreator.createBean(new Bean(clazz, BeanType.VIEW_FORM_CREATE), beanConfiguration));
                    mappedBean.setCreateView(BeanCreator.createBean(new Bean(clazz, BeanType.VIEW_CREATE), beanConfiguration));
                    mappedBean.setListView(BeanCreator.createBean(new Bean(clazz, BeanType.VIEW_LIST), beanConfiguration));
                    mappedBean.setMenu(BeanCreator.createBean(new Bean(clazz, BeanType.VIEW_MENU), beanConfiguration));
                    mappedBean.setDetail(BeanCreator.createBean(new Bean(clazz, BeanType.VIEW_DETAIL), beanConfiguration));
                    mappedBeans.add(mappedBean);
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                } catch (TemplateException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
        sortMappedBean(mappedBeans);
        return mappedBeans;
    }

    public void sortMappedBean(List<MappedBean> mappedBeans) {
        Collections.sort(mappedBeans, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((MappedBean) o1).getClass().getName().compareTo(((MappedBean) o2).getClass().getName());
            }
        });
    }

    public void sortClass(List<Class> classes) {
        Collections.sort(classes, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((Class) o1).getName().compareTo(((Class) o2).getName());
            }
        });
    }

    public List<MappedBean> getMappedBeans(BeanConfiguration beanConfiguration) {
        return getMappedBeans(getMappedClasses(), beanConfiguration);
    }

    public List<Class> getMappedClasses() {
        return getMappedClasses(false);
    }

    public boolean extendsAuditClasses(Class entity) {
        if (entity.getSuperclass().equals(AbstractAuditing.class) || entity.getSuperclass().equals(AbstractMetadata.class)) {
            return true;
        }
        if (!entity.getSuperclass().equals(Object.class)) {
            return extendsAuditClasses(entity.getSuperclass());
        }
        return false;
    }

    public List<Class> getMappedClasses(boolean includeEnum) {
        List<Class> mappedEntities = EntityUtils.getMappedEntities(entityManager);
        List<Class> classes = new ArrayList<>();
        for (Class entity : mappedEntities) {
            //do not include Audit classes
            if (!extendsAuditClasses(entity)) {
                classes.add(entity);
            }
            //add enum
            if (includeEnum) {
                for (Field field : entity.getDeclaredFields()) {
                    if (field.getType().isEnum() && !classes.contains(field.getType())) {
                        classes.add(field.getType());
                    }
                }
            }
        }
        sortClass(classes);
        return classes;
    }

    public String getClassBean(BeanConfiguration configuration) {
        return BeanCreator.getClassBean(getMappedClasses(true), configuration);

    }

    public SessionFactory getSessionFactory() {
        return getSession().getSessionFactory();
    }

    public Session getSession() {
        return entityManager.unwrap(Session.class);
    }
}
