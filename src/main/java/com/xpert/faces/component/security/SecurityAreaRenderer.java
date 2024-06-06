package com.xpert.faces.component.security;

import com.xpert.security.SecuritySessionManager;
import java.io.IOException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class SecurityAreaRenderer extends Renderer {

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
