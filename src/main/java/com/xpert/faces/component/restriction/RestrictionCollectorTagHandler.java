package com.xpert.faces.component.restriction;

import com.xpert.faces.component.initializer.InitializerEventListener;
import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class RestrictionCollectorTagHandler extends TagHandler {

    private final TagAttribute addTo;
    private final TagAttribute property;
    private final TagAttribute type;

    public RestrictionCollectorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.addTo = getAttribute("addTo");
        this.property = getAttribute("property");
        this.type = getAttribute("type");
    }

    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression addToVE = null;
            ValueExpression propertyVE = null;
            ValueExpression typeVE = null;

            if (addTo != null) {
                addToVE = addTo.getValueExpression(faceletContext, Object.class);
            }

            if (property != null) {
                propertyVE = property.getValueExpression(faceletContext, Object.class);
            }

            if (type != null) {
                typeVE = type.getValueExpression(faceletContext, Object.class);
            }

            EditableValueHolder valueHolder = (EditableValueHolder) parent;

            parent.subscribeToEvent(PostValidateEvent.class, new RestrictionCollector(addToVE, propertyVE, typeVE));
            
            //valueHolder.addValueChangeListener(new RestrictionCollector(addToVE, propertyVE, typeVE));

        }
    }
}
