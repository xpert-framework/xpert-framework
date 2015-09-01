package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * CPF converter. Show a CPF with mask and remove mask when submit
 *
 * @author Ayslan
 */
public class CpfConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String cpf = "";

        if (value != null) {
            cpf = value.replaceAll("[^\\d]", "");
        }

        return cpf;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String cpf = "";

        if (value != null && !value.toString().isEmpty()) {
            cpf = Mask.maskCpf(value.toString());
        }

        return cpf;
    }
}
