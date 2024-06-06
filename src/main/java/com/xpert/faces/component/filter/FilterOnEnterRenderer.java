package com.xpert.faces.component.filter;

import java.io.IOException;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class FilterOnEnterRenderer extends Renderer {

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final FilterOnEnter filterOnEnter = (FilterOnEnter) component;

        String target = "";
        if (filterOnEnter.getTarget() != null && !filterOnEnter.getTarget().isEmpty()) {
            UIComponent targetComponent = component.findComponent(filterOnEnter.getTarget());
            if (targetComponent == null) {
                throw new FacesException("Cannot find component " + filterOnEnter.getTarget() + " in view.");
            }else{
                target = targetComponent.getClientId(context);
            }
        }

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write(getScript(target, filterOnEnter));
        writer.endElement("script");
    }

    public String getScript(String target, FilterOnEnter filterOnEnter) {

        StringBuilder script = new StringBuilder();
        script.append("$(function() {Xpert.filterOnEnter('");
        script.append(target).append("','");
        script.append(filterOnEnter.getSelector()).append("');});");

        return script.toString();
    }
}
