package com.xpert.jasper;

import com.xpert.DAO;
import com.xpert.persistence.dao.BaseDAO;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;
import jakarta.persistence.EntityManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author ayslan
 */
public class JRBeanCollectionDataSource extends net.sf.jasperreports.engine.data.JRAbstractBeanDataSource {

    private static final Logger logger = Logger.getLogger(JRBeanCollectionDataSource.class.getName());
    private BaseDAO baseDAO;
    /**
     *
     */
    private Collection<?> data;
    private Iterator<?> iterator;
    private Object currentBean;

    public JRBeanCollectionDataSource(Collection<?> beanCollection) {
        this(beanCollection, true, null);
    }

    public JRBeanCollectionDataSource(Collection<?> beanCollection, EntityManager entityManager) {
        this(beanCollection, true, entityManager);
    }

    /**
     *
     * @param beanCollection
     * @param entityManager
     * @param isUseFieldDescription
     */
    public JRBeanCollectionDataSource(Collection<?> beanCollection, boolean isUseFieldDescription, EntityManager entityManager) {
        super(isUseFieldDescription);
        if (entityManager != null) {
            baseDAO = new DAO(entityManager);
            beanCollection = (Collection<?>) baseDAO.getInitialized(beanCollection);
        }
        this.data = beanCollection;
        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    @Override
    protected Object getFieldValue(Object bean, JRField field) throws JRException {
        if (baseDAO != null) {
            bean = baseDAO.getInitialized(bean);
        }
        return super.getFieldValue(bean, field);
    }

    public boolean next() {
        boolean hasNext = false;

        if (this.iterator != null) {
            hasNext = this.iterator.hasNext();

            if (hasNext) {
                this.currentBean = this.iterator.next();
            }
        }

        return hasNext;
    }

    /**
     *
     * @param field
     * @return 
     * @throws net.sf.jasperreports.engine.JRException 
     */
    public Object getFieldValue(JRField field) throws JRException {
        return getFieldValue(currentBean, field);
    }

    /**
     *
     */
    public void moveFirst() {
        if (this.data != null) {
            this.iterator = this.data.iterator();
        }
    }

    /**
     * Returns the underlying bean collection used by this data source.
     *
     * @return the underlying bean collection
     */
    public Collection<?> getData() {
        return data;
    }

    /**
     * Returns the total number of records/beans that this data source contains.
     *
     * @return the total number of records of this data source
     */
    public int getRecordCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * Clones this data source by creating a new instance that reuses the same
     * underlying bean collection.
     *
     * @return a clone of this data source
     */
    public JRBeanCollectionDataSource cloneDataSource() {
        return new JRBeanCollectionDataSource(data);
    }
}
