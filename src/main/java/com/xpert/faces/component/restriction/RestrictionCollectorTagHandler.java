package com.xpert.faces.component.restriction;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class RestrictionCollectorTagHandler extends TagHandler {

    private final TagAttribute addTo;
    private final TagAttribute debug;

    public RestrictionCollectorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.addTo = getAttribute("addTo");
        this.debug = getAttribute("debug");
    }

    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression addToVE = null;
            ValueExpression debugVE = null;

            if (addTo != null) {
                addToVE = addTo.getValueExpression(faceletContext, Object.class);
            }
            if (debug != null) {
                debugVE = debug.getValueExpression(faceletContext, Object.class);
            }

            //add action listener
            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new RestrictionCollector(addToVE, debugVE));

        }
    }
}
