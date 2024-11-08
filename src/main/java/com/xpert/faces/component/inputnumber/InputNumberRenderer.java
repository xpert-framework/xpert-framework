package com.xpert.faces.component.inputnumber;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.NumberConverter;
import org.primefaces.component.inputtext.InputTextRenderer;

/**
 * Renderer to the component "inputNumber"
 *
 * @author ayslan
 */
public class InputNumberRenderer extends InputTextRenderer {

    
    
    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {

        InputNumber inputNumber = (InputNumber) component;
        if (inputNumber.getConverter() != null && inputNumber.getConverter() instanceof NumberConverter) {
            NumberConverter numberConverter = (NumberConverter) inputNumber.getConverter();
            numberConverter.setMaxFractionDigits(inputNumber.getCentsLimit());
            numberConverter.setMinFractionDigits(inputNumber.getCentsLimit());
        }
     
        super.encodeEnd(context, component);

        //add script
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = component.getClientId(context);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write(getScript(clientId, inputNumber));
        writer.endElement("script");

    }

    public String getScript(String target, InputNumber inputNumber) {

        StringBuilder script = new StringBuilder();
        script.append("$(function() {$(Xpert.escapeClientId('").append(target).append("')).priceFormat({");
        script.append("limit: ").append(inputNumber.getLimit()).append(", ");
        script.append("prefix: '', ");
        script.append("centsSeparator: '").append(inputNumber.getCentsSeparator() == null ? getDecimalSeparator() : inputNumber.getCentsSeparator()).append("', ");
        script.append("thousandsSeparator: '").append(inputNumber.getThousandsSeparator() == null ? getGroupingSeparator() : inputNumber.getThousandsSeparator()).append("', ");
        script.append("allowNegative: ").append(inputNumber.getAllowNegative()).append(", ");
        script.append("centsLimit: ").append(inputNumber.getCentsLimit()).append("");
        script.append("});});");
        return script.toString();
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return new DecimalFormatSymbols(FacesContext.getCurrentInstance().getViewRoot().getLocale());
    }

    public char getDecimalSeparator() {
        return getDecimalFormatSymbols().getDecimalSeparator();
    }

    public char getGroupingSeparator() {
        return getDecimalFormatSymbols().getGroupingSeparator();
    }

}
