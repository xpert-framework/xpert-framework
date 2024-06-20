package com.xpert.audit;

import com.xpert.AuditDAO;
import com.xpert.Configuration;
import com.xpert.audit.model.AbstractAuditing;
import com.xpert.audit.model.AbstractMetadata;
import com.xpert.audit.model.AuditingType;
import com.xpert.faces.primefaces.LazyDataModelImpl;
import com.xpert.persistence.dao.BaseDAO;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import com.xpert.persistence.utils.EntityUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author ayslan
 */
@Named("auditDeleteBean")
@ViewScoped
public class AuditDeleteBean implements Serializable {

    private static final long serialVersionUID = -2653891634297925727L;

    private static final Logger logger = Logger.getLogger(AuditDeleteBean.class.getName());
    private Class entityClass;
    private LazyDataModel<AbstractAuditing> auditings;
    private BaseDAO baseDAO;

    @PostConstruct
    public void init() {
        baseDAO = new AuditDAO(Configuration.getAuditingImplClass());
    }

    public void load(Class clazz) {
        this.entityClass = clazz;
        load();
    }

    public void load() {
        if (entityClass != null) {
            List<Restriction> restrictions = new ArrayList<>();
            restrictions.add(new Restriction("entity", Audit.getEntityName(entityClass)));
            restrictions.add(new Restriction("auditingType", AuditingType.DELETE));
            auditings = new LazyDataModelImpl<>("eventDate DESC", restrictions, baseDAO);
        }
    }

    public List<AbstractAuditing> getInsertsAndUpdates(AbstractAuditing deleteAuditing) {

        List<AuditingType> insertUpdateTypes = new ArrayList<>();
        insertUpdateTypes.add(AuditingType.INSERT);
        insertUpdateTypes.add(AuditingType.UPDATE);

        List<Restriction> restrictions = new ArrayList<>();
        restrictions.add(new Restriction("identifier", deleteAuditing.getIdentifier()));
        restrictions.add(new Restriction("entity", deleteAuditing.getEntity()));
        restrictions.add(new Restriction("auditingType", RestrictionType.IN, insertUpdateTypes));

        return baseDAO.list(restrictions, "eventDate DESC");
    }

    public Object newBeanInstance(AbstractAuditing auditingDelete) {
        try {

            Object newInstance = entityClass.getDeclaredConstructor().newInstance();
            PropertyUtils.setProperty(newInstance, EntityUtils.getIdFieldName(entityClass), auditingDelete.getIdentifier());

            return newInstance;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getObjectDescripton(List<AbstractMetadata> metadatas) {
        if (metadatas == null || metadatas.isEmpty()) {
            return "";
        }
        return metadatas.stream()
                .map(metadata -> String.format("<b>%s: </b>%s", metadata.getField(), metadata.getNewValue()))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entity) {
        this.entityClass = entity;
    }

    public LazyDataModel<AbstractAuditing> getAuditings() {
        return auditings;
    }

    public void setAuditings(LazyDataModel<AbstractAuditing> auditings) {
        this.auditings = auditings;
    }

    public BaseDAO getBaseDAO() {
        return baseDAO;
    }

}
