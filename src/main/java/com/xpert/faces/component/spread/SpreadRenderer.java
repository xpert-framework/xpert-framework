package com.xpert.faces.component.spread;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class SpreadRenderer extends Renderer {

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
