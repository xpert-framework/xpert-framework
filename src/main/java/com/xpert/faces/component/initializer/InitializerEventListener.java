package com.xpert.faces.component.initializer;

import java.io.Serializable;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;
import javax.faces.view.facelets.FaceletContext;

/**
 *
 * @author ayslan
 */
public class InitializerEventListener implements ComponentSystemEventListener, Serializable {


    private String property;
    private ValueExpression valueExpression;
    private FaceletContext faceletContext;
    private InitializerBean initializerBean;
    private UIComponent parent;

    public InitializerEventListener() {
    }

    public InitializerEventListener(String property, ValueExpression valueExpression,
            FaceletContext faceletContext, UIComponent parent, InitializerBean initializerBean) {
        this.property = property;
        this.valueExpression = valueExpression;
        this.faceletContext = faceletContext;
        this.parent = parent;
        this.initializerBean = initializerBean;
    }

    

    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        if (parent == null || faceletContext == null) {
            return;
        }
        if (valueExpression == null) {
            initializerBean.initialize(parent, faceletContext.getFacesContext(), property);
        } else {
            initializerBean.initialize(parent, faceletContext.getFacesContext(), valueExpression);
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
