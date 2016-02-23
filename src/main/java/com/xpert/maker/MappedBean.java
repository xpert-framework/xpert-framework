package com.xpert.maker;

import com.xpert.utils.HumaniseCamelCase;

/**
 *
 * @author ayslan
 */
public class MappedBean {

    private Class entityClass;
    private String className;
    private String i18n;
    private String managedBean;
    private String businnesObject;
    private String dao;
    private String daoImpl;
    //XHTML
    private String createView;
    private String formCreateView;
    private String listView;
    private String menu;
    private String detail;

    public MappedBean() {
    }

    public String getHumanClassName() {
        if (entityClass != null) {
            return new HumaniseCamelCase().humanise(entityClass.getSimpleName());
        }
        return null;
    }

    public String getClassName() {
        if (entityClass != null) {
            return entityClass.getName();
        }
        return className;
    }

    public MappedBean(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getCreateView() {
        return createView;
    }

    public void setCreateView(String createView) {
        this.createView = createView;
    }

    public String getFormCreateView() {
        return formCreateView;
    }

    public void setFormCreateView(String formCreateView) {
        this.formCreateView = formCreateView;
    }

    public String getListView() {
        return listView;
    }

    public void setListView(String listView) {
        this.listView = listView;
    }

    public String getDaoImpl() {
        return daoImpl;
    }

    public void setDaoImpl(String daoImpl) {
        this.daoImpl = daoImpl;
    }

    public String getBusinnesObject() {
        return businnesObject;
    }

    public void setBusinnesObject(String businnesObject) {
        this.businnesObject = businnesObject;
    }

    public String getDao() {
        return dao;
    }

    public void setDao(String dao) {
        this.dao = dao;
    }

    public String getManagedBean() {
        return managedBean;
    }

    public void setManagedBean(String managedBean) {
        this.managedBean = managedBean;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }
}
