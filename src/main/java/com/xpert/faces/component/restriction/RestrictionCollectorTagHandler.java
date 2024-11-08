package com.xpert.faces.component.restriction;

import java.io.IOException;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.ActionSource;
import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.FaceletException;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;

/**
 * TagHandler for the component "x:restrictionCollector".
 * This taghandler add a ActionListener (RestrictionCollector) to the current component
 * 
 * @author ayslan
 */
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
