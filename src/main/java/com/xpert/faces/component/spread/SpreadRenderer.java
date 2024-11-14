package com.xpert.faces.component.spread;

import java.io.IOException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.Renderer;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class SpreadRenderer extends Renderer implements Serializable {

    private static final long serialVersionUID = -786741614075575914L;

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final Spread confirmation = (Spread) component;
        final String clientId = confirmation.getParent().getClientId(context);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write(getScript(clientId, confirmation));
        writer.endElement("script");
    }

    public String getScript(String target, Spread confirmation) {

        StringBuilder script = new StringBuilder();
        script.append("$(function() {Xpert.spreadCheckBoxList('").append(target).append("',");
        script.append(confirmation.getColumns()).append(",");
        script.append(confirmation.getHighlight());
        script.append(")});");
        return script.toString();
    }
}
