package com.xpert.audit;

import com.xpert.AuditDAO;
import com.xpert.Configuration;
import com.xpert.audit.model.AbstractAuditing;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.utils.EntityUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author ayslan
 */
@Named("auditBean")
@ViewScoped
public class AuditBean implements Serializable {

    private static final long serialVersionUID = -2671979402785945505L;

    private Object object;
    private Map<BeanModel, DetailAuditBean> beans = new HashMap<>();
    private BaseDAO baseDAO;
    private BeanModel lastBean;
    private DetailAuditBean lastModel;
    private boolean enableAudit;

    @PostConstruct
    public void init() {
        baseDAO = new AuditDAO(Configuration.getAuditingImplClass());
        this.enableAudit = false;
    }

    public void detail(Object object) {
        this.object = object;
        if (this.object != null) {
            detail();
        }
    }

    public void detail() {
        BeanModel beanModel = getBeanModel(object);
        DetailAuditBean detail = beans.get(beanModel);
        if (detail == null && beanModel != null) {
            detail = new DetailAuditBean(beanModel, baseDAO);
            detail.load();
            beans.put(beanModel, detail);
        }
        this.enableAudit = true;
    }

    private BeanModel getBeanModel(Object object) {
        if (object == null) {
            return null;
        }
        Object id = null;
        String entity = null;
        if (object instanceof AbstractAuditing) {
            id = ((AbstractAuditing) object).getIdentifier();
            entity = ((AbstractAuditing) object).getEntity();
        } else {
            id = EntityUtils.getId(object);
            entity = Audit.getEntityName(object.getClass());
        }
        return new BeanModel(id, entity);
    }

    public DetailAuditBean getDetailAuditBean(Object object) {
        BeanModel beanModel = getBeanModel(object);

        if (beanModel == null && lastBean != null) {
            beanModel = lastBean;
        }

        if (lastBean != null && lastModel != null && beanModel != null && beanModel.equals(lastBean)) {
            return lastModel;
        }

        if (beanModel != null) {
            DetailAuditBean detail = getBeans().get(beanModel);
            if (detail != null) {
                lastBean = beanModel;
                lastModel = detail;
                return detail;
            }
        }
        //prevent nullpointer
        return new DetailAuditBean();
    }

    public boolean isPersisted(Object object) {
        if (object != null && !Audit.isAudit(object.getClass())) {
            return false;
        }
        if (EntityUtils.getId(object) != null) {
            return true;
        }
        return false;
    }

    public void loadMetadatas(Object object) {
        AbstractAuditing auditing = ((AbstractAuditing) object);
        if (!auditing.isExpanded()) {
            auditing.setMetadatasLazy((List) baseDAO.getInitialized(auditing.getMetadatas()));
        }
        auditing.setExpanded(!auditing.isExpanded());
    }

    public Map<BeanModel, DetailAuditBean> getBeans() {
        return beans;
    }

    public void setBeans(Map<BeanModel, DetailAuditBean> beans) {
        this.beans = beans;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public BaseDAO getBaseDAO() {
        return baseDAO;
    }

    public boolean isEnableAudit() {
        return enableAudit;
    }

    public void setEnableAudit(boolean enableAudit) {
        this.enableAudit = enableAudit;
    }

}
