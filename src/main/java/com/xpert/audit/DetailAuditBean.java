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
        //Long doesnt need conversion
        if (beanModel.getId() instanceof Long) {
            restrictions.add(new Restriction("identifier", beanModel.getId()));
        } else if (beanModel.getId() instanceof Number) {
            //if is a number try to convert to long
            restrictions.add(new Restriction("identifier", ((Number) beanModel.getId()).longValue()));
        } else {
            throw new IllegalArgumentException("Type of id " + beanModel.getId().getClass().getName() + " from class " + beanModel.getEntity() + " is not supported in audit");
        }
        restrictions.add(new Restriction("entity", beanModel.getEntity()));

        return restrictions;
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
