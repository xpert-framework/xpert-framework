package com.xpert.faces.component.initializer;

import java.io.Serializable;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.view.facelets.FaceletContext;
import javax.persistence.EntityManager;

/**
 *
 * @author ayslan
 */
public class InitializerEventListener implements ComponentSystemEventListener, Serializable {

    private String property;
    private ValueExpression valueExpression;
    private FaceletContext faceletContext;
    private InitializerBean initializerBean;
    private EntityManager entityManager;
    private UIComponent parent;

    public InitializerEventListener() {
    }

    public InitializerEventListener(String property, ValueExpression valueExpression,
            FaceletContext faceletContext, UIComponent parent, InitializerBean initializerBean, EntityManager entityManager) {
        this.property = property;
        this.valueExpression = valueExpression;
        this.faceletContext = faceletContext;
        this.parent = parent;
        this.initializerBean = initializerBean;
        this.entityManager = entityManager;
    }

    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (parent == null || faceletContext == null) {
            return;
        }
        if (valueExpression == null) {
            initializerBean.initialize(parent, faceletContext.getFacesContext(), property, entityManager);
        } else {
            initializerBean.initialize(parent, faceletContext.getFacesContext(), valueExpression, entityManager);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InitializerEventListener other = (InitializerEventListener) obj;
        if (this.parent != other.parent && (this.parent == null || !this.parent.equals(other.parent))) {
            return false;
        }
        return true;
    }

}
