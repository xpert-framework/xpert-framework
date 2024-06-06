package com.xpert.faces.component.mask;

import java.io.IOException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.Renderer;
import org.primefaces.component.calendar.Calendar;

/**
 * Renderer to "mask" component
 *
 * @author ayslan
 */
public class MaskRenderer extends Renderer {

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final Mask mask = (Mask) component;
        if (mask.getParent() instanceof Calendar) {
            final ResponseWriter writer = context.getResponseWriter();
            final Calendar calendar = (Calendar) mask.getParent();
            final String clientId = mask.getParent().getClientId(context);
            String pattern = null;
            if (calendar.getPattern() != null && !calendar.getPattern().isEmpty()) {
                pattern = calendar.getPattern();
            } else {
                pattern = calendar.isTimeOnly() ? calendar.calculateTimeOnlyPattern() : calendar.calculatePattern();
            }
            String maskPattern = pattern.replaceAll("[a-zA-Z]", "9");
            writer.startElement("script", null);
            writer.write("$(function(){$(PrimeFaces.escapeClientId('");
            writer.write(clientId + "_input");
            writer.write("')).mask('");
            writer.write(maskPattern);
            writer.write("'");
            if (mask.getPlaceHolder() != null) {
                writer.write(",{placeholder:''}");
            }
            writer.write(");});");
            writer.endElement("script");
        }
    }

}
