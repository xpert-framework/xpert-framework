package com.xpert.faces.component.confirmation;

import com.xpert.faces.primefaces.PrimeFacesUtils;
import com.xpert.i18n.XpertResourceBundle;
import com.xpert.utils.StringEscapeUtils;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

/**
 *
 * @author ayslan
 */
public class ConfirmationRenderer extends Renderer {


    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final Confirmation confirmation = (Confirmation) component;
        final String clientId = confirmation.getParent().getClientId(context);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write(getScript(clientId, confirmation));
        writer.endElement("script");
    }

    public String getScript(String target, Confirmation confirmation) {

        if (confirmation.getMessage() == null || confirmation.getMessage().isEmpty()) {
            confirmation.setMessage(XpertResourceBundle.get("confirm"));
        }
        if (confirmation.getConfirmLabel() == null || confirmation.getConfirmLabel().isEmpty()) {
            confirmation.setConfirmLabel(XpertResourceBundle.get("yes"));
        }
        if (confirmation.getCancelLabel() == null || confirmation.getCancelLabel().isEmpty()) {
            confirmation.setCancelLabel(XpertResourceBundle.get("no"));
        }

        StringBuilder script = new StringBuilder();

        script.append("$(function() {Xpert.behavior.verifyConfirmation($(PrimeFaces.escapeClientId('").append(target).append("'))");
        script.append(",'").append(StringEscapeUtils.escapeJavaScript(confirmation.getConfirmLabel())).append("'");
        script.append(",'").append(StringEscapeUtils.escapeJavaScript(confirmation.getCancelLabel())).append("'");
        script.append(",'").append(StringEscapeUtils.escapeJavaScript(confirmation.getMessage())).append("'");
        script.append(",").append(PrimeFacesUtils.isVersion3());
        script.append(");});");


        return script.toString();
    }
}
