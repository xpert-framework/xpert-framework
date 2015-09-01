package com.xpert.faces.component.security;

import com.xpert.security.SecuritySessionManager;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class SecurityAreaRenderer extends Renderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        SecurityArea securityArea = (SecurityArea) component;
        if (securityArea.isRendered() && securityArea.getChildCount() > 0) {
            if (SecuritySessionManager.hasRole(securityArea.getRolesAllowed(), context)) {
                for (UIComponent child : securityArea.getChildren()) {
                    if (child.isRendered()) {
                        child.encodeAll(context);
                    }
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
