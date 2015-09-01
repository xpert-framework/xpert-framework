package com.xpert.faces.bean;

import com.xpert.Configuration;
import com.xpert.faces.bootstrap.BootstrapVersion;
import com.xpert.faces.primefaces.PrimeFacesVersion;
import com.xpert.faces.utils.FacesUtils;
import com.xpert.maker.MappedBean;
import com.xpert.maker.BeanConfiguration;
import com.xpert.maker.PersistenceMappedBean;
import com.xpert.maker.BeanCreator;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

/**
 *
 * @author Ayslan
 */
public class BeanMaker implements Serializable {

    private static final Logger logger = Logger.getLogger(BeanMaker.class.getName());
    private final SimpleDateFormat ZIP_NAME_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH'h'-mm'm'");
    private EntityManager entityManager;
    private List<MappedBean> mappedBeans;
    private List<Class> selectedClasses;
    private List<Class> classes;
    private List<String> nameSelectedClasses = new ArrayList<String>();
    private PersistenceMappedBean persistenceMappedBean;
    private BeanConfiguration configuration = new BeanConfiguration();
    private String author;
    private String classBean;
    private String menubar;
    private String viewTemplate;
    private String i18n;

    @PostConstruct
    public void init() {
        entityManager = Configuration.getEntityManager();
        persistenceMappedBean = new PersistenceMappedBean(entityManager);
        classes = persistenceMappedBean.getMappedClasses();
        load();
    }

    public void make() {
        selectedClasses = new ArrayList<Class>();
        for (String className : nameSelectedClasses) {
            try {
                selectedClasses.add(Class.forName(className, true, Thread.currentThread().getContextClassLoader()));
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        mappedBeans = persistenceMappedBean.getMappedBeans(selectedClasses, configuration);
        load();
    }

    public void makeAll() {
        mappedBeans = persistenceMappedBean.getMappedBeans(configuration);
        for (Class clazz : classes) {
            nameSelectedClasses.add(clazz.getName());
        }
        load();
    }

    private void load() {
        classBean = BeanCreator.getClassBean(selectedClasses, configuration);
        if (mappedBeans != null) {
            menubar = BeanCreator.getMenubar(mappedBeans, configuration.getResourceBundle(), configuration);
            i18n = BeanCreator.getI18N(mappedBeans);
        }
        viewTemplate = BeanCreator.getViewTemplate();
    }

    public void download() {
        try {
            FacesUtils.download(BeanCreator.createBeanZipFile(mappedBeans, classBean, viewTemplate, configuration), "application/zip", getDownloadFileName());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, ex.getMessage(), null));
        }
    }

    public String getDownloadFileName() {
        return "beans_" + ZIP_NAME_DATE_FORMAT.format(new Date()) + ".zip";
    }

    public void reset() {
        nameSelectedClasses = new ArrayList<String>();
        mappedBeans = new ArrayList<MappedBean>();
    }
    
    public PrimeFacesVersion[] getPrimeFacesVersions(){
        return PrimeFacesVersion.values();
    }
    public BootstrapVersion[] getBootstrapVersions(){
        return BootstrapVersion.values();
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    public List<String> getNameSelectedClasses() {
        return nameSelectedClasses;
    }

    public void setNameSelectedClasses(List<String> nameSelectedClasses) {
        this.nameSelectedClasses = nameSelectedClasses;
    }

    public List<MappedBean> getMappedBeans() {
        return mappedBeans;
    }

    public void setMappedBeans(List<MappedBean> mappedBeans) {
        this.mappedBeans = mappedBeans;
    }

    public BeanConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(BeanConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getClassBean() {
        return classBean;
    }

    public void setClassBean(String classBean) {
        this.classBean = classBean;
    }

    public String getViewTemplate() {
        return viewTemplate;
    }

    public void setViewTemplate(String viewTemplate) {
        this.viewTemplate = viewTemplate;
    }

    public String getMenubar() {
        return menubar;
    }

    public void setMenubar(String menubar) {
        this.menubar = menubar;
    }
}
