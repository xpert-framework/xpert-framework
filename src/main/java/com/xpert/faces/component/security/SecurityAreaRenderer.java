package com.xpert.faces.component.security;

import java.io.IOException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.Renderer;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class SecurityAreaRenderer extends Renderer implements Serializable {

    private static final long serialVersionUID = -8712402607305273451L;

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SecurityArea securityArea = (SecurityArea) component;
        if (securityArea.isRendered() && securityArea.getChildCount() > 0 && securityArea.hasRole(context)) {
            for (UIComponent child : securityArea.getChildren()) {
                if (child.isRendered()) {
                    child.encodeAll(context);
                }
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
