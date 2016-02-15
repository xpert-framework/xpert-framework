package com.xpert.faces.component.inputnumber;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.NumberConverter;
import org.primefaces.component.inputtext.InputTextRenderer;

/**
 *
 * @author Ayslan
 */
public class InputNumberRenderer extends InputTextRenderer {

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        super.encodeEnd(context, component);

        InputNumber inputNumber = (InputNumber) component;
        if (inputNumber.getConverter() == null) {
            NumberConverter numberConverter = new NumberConverter();
            numberConverter.setMaxFractionDigits(inputNumber.getCentsLimit());
            numberConverter.setMinFractionDigits(inputNumber.getCentsLimit());
            numberConverter.setLocale(context.getViewRoot().getLocale());
            inputNumber.setConverter(numberConverter);
        }
        
        //add script
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = component.getClientId(context);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write(getScript(clientId, inputNumber));
        writer.endElement("script");

    }

    public String getScript(String target, InputNumber inputNumber) {

        //$(function() {$(PrimeFaces.escapeClientId('#{cc.attrs.clientId}:input')).priceFormat({
        //limit: #{cc.attrs.limit}, prefix: '',
        // centsSeparator: '#{cc.attrs.centsSeparator == null ? localeBean.decimalSeparator : cc.attrs.centsSeparator}', 
        //thousandsSeparator: '#{cc.attrs.thousandsSeparator == null ? localeBean.groupingSeparator : cc.attrs.thousandsSeparator}', 
        //allowNegative:#{cc.attrs.allowNegative},
        //centsLimit: #{cc.attrs.centsLimit}}
        //);});
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
