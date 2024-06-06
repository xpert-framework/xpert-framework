package com.xpert.faces.component.initializer;

import java.io.IOException;
import java.util.Map;
import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PreRenderComponentEvent;
import jakarta.faces.view.facelets.ComponentConfig;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;
import jakarta.persistence.EntityManager;

/**
 *
 * @author ayslan
 */
public class InitializerHandler extends TagHandler {

    protected final TagAttribute property;
    protected final TagAttribute value;
    protected final TagAttribute entityManager;
    public static final String INITILIZER_BEAN_IDENTIFIER = "xpert.initializer.initializerBean";

    public InitializerHandler(TagConfig config) {
        super(config);
        this.value = this.getAttribute("value");
        this.property = this.getAttribute("property");
        this.entityManager = this.getAttribute("entityManager");
    }

    public InitializerHandler(ComponentConfig config) {
        super(config);
        this.value = this.getAttribute("value");
        this.property = this.getAttribute("property");
        this.entityManager = this.getAttribute("entityManager");
    }

    public InitializerBean getInitializerBean(FacesContext context, EntityManager entityManager) {
        Map requestMap = context.getExternalContext().getRequestMap();
        InitializerBean initializerBean = (InitializerBean) requestMap.get(INITILIZER_BEAN_IDENTIFIER);
        if (initializerBean != null) {
            return initializerBean;
        } else {
            initializerBean = new InitializerBean(entityManager);
            requestMap.put(INITILIZER_BEAN_IDENTIFIER, initializerBean);
            return initializerBean;
        }
    }

    public void apply(final FaceletContext faceletContext, final UIComponent parent) throws IOException {
        if (ComponentHandler.isNew(parent)) {

            EntityManager entityManager = getEntityManager(faceletContext);
            
            ValueExpression valueExpression = getValueExpression(faceletContext);
            InitializerBean initializerBean = getInitializerBean(faceletContext.getFacesContext(), entityManager);

            InitializerEventListener initializerEventListener = new InitializerEventListener(
                    getProperty(faceletContext), valueExpression, faceletContext, parent, initializerBean, entityManager);

            parent.subscribeToEvent(PreRenderComponentEvent.class, initializerEventListener);
        }
    }

    public String getProperty(FaceletContext faceletContext) {
        if (property != null) {
            ValueExpression valueExpression = property.getValueExpression(faceletContext, String.class);
            if (valueExpression != null) {
                return (String) valueExpression.getValue(faceletContext);

            }
        }
        return null;
    }

    public EntityManager getEntityManager(FaceletContext faceletContext) {
        if (entityManager != null) {
            ValueExpression valueExpression = entityManager.getValueExpression(faceletContext, EntityManager.class);
            if (valueExpression != null) {
                return (EntityManager) valueExpression.getValue(faceletContext);

            }
        }
        return null;
    }

    public ValueExpression getValueExpression(FaceletContext faceletContext) {
        if (value != null) {
            return value.getValueExpression(faceletContext, Object.class);
        }
        return null;
    }
}
