package com.xpert.maker;

import com.xpert.faces.bootstrap.BootstrapVersion;
import com.xpert.faces.primefaces.PrimeFacesVersion;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class BeanConfiguration implements Serializable {

    private static final long serialVersionUID = -3971038246937421245L;

    private String managedBean;
    private String businessObject;
    private String dao;
    private String daoImpl;
    private String baseDAO;
    private String author;
    private String resourceBundle;
    private String template;
    private String viewPath;

    //location attributes
    private String managedBeanLocation;
    private String businessObjectLocation;
    private String daoLocation;
    private String daoImplLocation;
    private String viewLocation;
    private String classManagedBeanLocation;
    private String resourceBundleLocation;
    private boolean classManagedBeanGenerated;
    private boolean resourceBundleGenerated;

    //suffix/preffix
    private String managedBeanSuffix;
    private String businessObjectSuffix;
    private PrimeFacesVersion primeFacesVersion;
    private BootstrapVersion bootstrapVersion;
    private boolean useCDIBeans;
    private boolean generatesSecurityArea;
    private boolean maskCalendar;
    //hide id in url
    private boolean hideIdInRequest;

    //date pattern to calendar
    private String datePattern;
    //time pattern to calendar
    private String timePattern;

    public BeanConfiguration() {
        this.timePattern = BeanCreator.DEFAULT_TIME_PATTERN;
        this.datePattern = BeanCreator.DEFAULT_DATE_PATTERN;
        this.managedBeanSuffix = BeanCreator.SUFFIX_MANAGED_BEAN;
        this.businessObjectSuffix = BeanCreator.SUFFIX_BUSINESS_OBJECT;
        this.primeFacesVersion = PrimeFacesVersion.VERSION_14;
        this.bootstrapVersion = null;
        this.generatesSecurityArea = true;
        this.maskCalendar = true;
        this.hideIdInRequest = false;
    }

    /**
     * Primefaces less than 5 uses "placeHolder" isntead of "slotChar" @return
     *
     * @return
     */
    public String getSlotCharName() {
        return "slotChar";
    }

    public boolean isHideIdInRequest() {
        return hideIdInRequest;
    }

    public void setHideIdInRequest(boolean hideIdInRequest) {
        this.hideIdInRequest = hideIdInRequest;
    }

    public String getResourceBundleLocation() {
        return resourceBundleLocation;
    }

    public void setResourceBundleLocation(String resourceBundleLocation) {
        this.resourceBundleLocation = resourceBundleLocation;
    }

    public boolean isResourceBundleGenerated() {
        return resourceBundleGenerated;
    }

    public void setResourceBundleGenerated(boolean resourceBundleGenerated) {
        this.resourceBundleGenerated = resourceBundleGenerated;
    }

    public boolean isClassManagedBeanGenerated() {
        return classManagedBeanGenerated;
    }

    public void setClassManagedBeanGenerated(boolean classManagedBeanGenerated) {
        this.classManagedBeanGenerated = classManagedBeanGenerated;
    }

    public String getClassManagedBeanLocation() {
        return classManagedBeanLocation;
    }

    public void setClassManagedBeanLocation(String classManagedBeanLocation) {
        this.classManagedBeanLocation = classManagedBeanLocation;
    }

    public String getTimePattern() {
        return timePattern;
    }

    public void setTimePattern(String timePattern) {
        this.timePattern = timePattern;
    }

    public boolean isMaskCalendar() {
        return maskCalendar;
    }

    public void setMaskCalendar(boolean maskCalendar) {
        this.maskCalendar = maskCalendar;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public boolean isGeneratesSecurityArea() {
        return generatesSecurityArea;
    }

    public void setGeneratesSecurityArea(boolean generatesSecurityArea) {
        this.generatesSecurityArea = generatesSecurityArea;
    }

    public boolean isUseCDIBeans() {
        return useCDIBeans;
    }

    public void setUseCDIBeans(boolean useCDIBeans) {
        this.useCDIBeans = useCDIBeans;
    }

    public BootstrapVersion getBootstrapVersion() {
        return bootstrapVersion;
    }

    public void setBootstrapVersion(BootstrapVersion bootstrapVersion) {
        this.bootstrapVersion = bootstrapVersion;
    }

    public PrimeFacesVersion getPrimeFacesVersion() {
        return primeFacesVersion;
    }

    public void setPrimeFacesVersion(PrimeFacesVersion primeFacesVersion) {
        this.primeFacesVersion = primeFacesVersion;
    }

    public String getBusinessObjectSuffix() {
        return businessObjectSuffix;
    }

    public void setBusinessObjectSuffix(String businessObjectSuffix) {
        this.businessObjectSuffix = businessObjectSuffix;
    }

    public String getManagedBeanSuffix() {
        return managedBeanSuffix;
    }

    public void setManagedBeanSuffix(String managedBeanSuffix) {
        this.managedBeanSuffix = managedBeanSuffix;
    }

    public String getManagedBeanLocation() {
        return managedBeanLocation;
    }

    public void setManagedBeanLocation(String managedBeanLocation) {
        this.managedBeanLocation = managedBeanLocation;
    }

    public String getBusinessObjectLocation() {
        return businessObjectLocation;
    }

    public void setBusinessObjectLocation(String businessObjectLocation) {
        this.businessObjectLocation = businessObjectLocation;
    }

    public String getDaoLocation() {
        return daoLocation;
    }

    public void setDaoLocation(String daoLocation) {
        this.daoLocation = daoLocation;
    }

    public String getDaoImplLocation() {
        return daoImplLocation;
    }

    public void setDaoImplLocation(String daoImplLocation) {
        this.daoImplLocation = daoImplLocation;
    }

    public String getViewLocation() {
        return viewLocation;
    }

    public void setViewLocation(String viewLocation) {
        this.viewLocation = viewLocation;
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getBaseDAO() {
        return baseDAO;
    }

    public void setBaseDAO(String baseDAO) {
        this.baseDAO = baseDAO;
    }

    public String getBaseDAOSimpleName() {
        if (baseDAO != null && baseDAO.lastIndexOf(".") > -1) {
            return baseDAO.substring(baseDAO.lastIndexOf(".") + 1, baseDAO.length());
        }
        return baseDAO;
    }

    public String getBaseDAOPackage() {
        if (baseDAO != null && baseDAO.lastIndexOf(".") > -1) {
            return baseDAO.substring(0, baseDAO.lastIndexOf("."));
        }
        return "";
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(String resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public String getBusinessObject() {
        return businessObject;
    }

    public void setBusinessObject(String businessObject) {
        this.businessObject = businessObject;
    }

    public String getDao() {
        return dao;
    }

    public void setDao(String dao) {
        this.dao = dao;
    }

    public String getDaoImpl() {
        return daoImpl;
    }

    public void setDaoImpl(String daoImpl) {
        this.daoImpl = daoImpl;
    }

    public String getManagedBean() {
        return managedBean;
    }

    public void setManagedBean(String managedBean) {
        this.managedBean = managedBean;
    }
}
