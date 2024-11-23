package com.xpert.audit;

import com.xpert.faces.primefaces.LazyDataModelImpl;
import com.xpert.audit.model.AbstractAuditing;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.utils.EntityUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author ayslan
 */
public class DetailAuditBean implements Serializable {

    private static final long serialVersionUID = 7498075390454510513L;

    private BeanModel beanModel;
    private LazyDataModel<AbstractAuditing> auditings;
    private BaseDAO baseDAO;

    public DetailAuditBean() {
    }

    public DetailAuditBean(BeanModel beanModel, BaseDAO baseDAO) {
        this.beanModel = beanModel;
        this.baseDAO = baseDAO;
    }

    public void load() {
        if (beanModel != null) {
            auditings = new LazyDataModelImpl<>("eventDate DESC", getRestrictions(), baseDAO);
        }
    }

    private List<Restriction> getRestrictions() {
        List<Restriction> restrictions = new ArrayList<>();

        Object id = beanModel.getId();
        if (id instanceof String value) {
            restrictions.add(new Restriction("identifier", value));
        } else if (id instanceof Long value) {
            restrictions.add(new Restriction("identifier", value));
        } else if (id instanceof Number value) {
            restrictions.add(new Restriction("identifier", value.longValue()));
        } else {
            throwUnsupportedIdTypeException(id);
        }

        restrictions.add(new Restriction("entity", beanModel.getEntity()));

        return restrictions;
    }

    private void throwUnsupportedIdTypeException(Object id) {
        String entityType = beanModel.getEntity();
        String idType = id != null ? id.getClass().getName() : "null";
        throw new IllegalArgumentException(String.format("Unsupported id type '%s' for entity '%s' in audit.", idType, entityType));
    }

    public boolean isPersisted(Object object) {
        return EntityUtils.getId(object) != null;
    }

    public LazyDataModel<AbstractAuditing> getAuditings() {
        return auditings;
    }

    public void setAuditings(LazyDataModel<AbstractAuditing> auditings) {
        this.auditings = auditings;
    }

    public BeanModel getBeanModel() {
        return beanModel;
    }

    public void setBeanModel(BeanModel beanModel) {
        this.beanModel = beanModel;
    }

}
