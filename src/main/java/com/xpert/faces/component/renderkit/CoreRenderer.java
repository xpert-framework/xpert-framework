package com.xpert.faces.component.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class CoreRenderer extends Renderer{

    protected void renderChildren(FacesContext context, UIComponent component) throws IOException {
        if (component.getChildCount() > 0) {
            for (int i = 0; i < component.getChildCount(); i++) {
                UIComponent child = (UIComponent) component.getChildren().get(i);
                renderChild(context, child);
            }
        }
    }

    protected void renderChild(FacesContext context, UIComponent child) throws IOException {
        if (!child.isRendered()) {
            return;
        }

        child.encodeBegin(context);

        if (child.getRendersChildren()) {
            child.encodeChildren(context);
        } else {
            renderChildren(context, child);
        }
        child.encodeEnd(context);
    }
}
