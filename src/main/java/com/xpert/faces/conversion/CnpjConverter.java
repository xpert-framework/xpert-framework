package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * CNPJ converter. Show a CNPJ with mask and remove mask when submit.
 *
 * @author Ayslan
 */
public class CnpjConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String cnpj = "";

        if (value != null) {
            cnpj = value.replaceAll("[^\\d]", "");
        }

        return cnpj;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String cnpj = "";

        if (value != null && !value.toString().isEmpty()) {
            cnpj = Mask.maskCnpj(value.toString());
        }

        return cnpj;
    }
}
