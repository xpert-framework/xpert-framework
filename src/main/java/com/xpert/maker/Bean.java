package com.xpert.maker;

import com.xpert.maker.model.ViewEntity;
import com.xpert.utils.StringUtils;

/**
 *
 * @author ayslan
 */
public class Bean {

    private Class entity;
    private BeanType beanType;
    private BeanConfiguration configuration;
    private String author;
    private String resourceBundle;
    private ViewEntity viewEntity;
    
    public Bean(Class entity, BeanType beanType) {
        this.entity = entity;
        this.beanType = beanType;
    }
    
    public String getNameLower() {
        return StringUtils.getLowerFirstLetter(getName());
    }

    public ViewEntity getViewEntity() {
        return viewEntity;
    }

    public void setViewEntity(ViewEntity viewEntity) {
        this.viewEntity = viewEntity;
    }
    
    public String getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(String resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
    
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BeanConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BeanConfiguration configuration) {
        this.configuration = configuration;
    }

    public BeanType getBeanType() {
        return beanType;
    }

    public void setBeanType(BeanType beanType) {
        this.beanType = beanType;
    }
    
    public Class getEntity() {
        return entity;
    }

    public void setEntity(Class entity) {
        this.entity = entity;
    }

    public String getName() {
        if (entity != null) {
            return entity.getSimpleName();
        }
        return null;
    }
}
