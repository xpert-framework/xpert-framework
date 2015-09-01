package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * CPF/CNPJ converter. Show a CPF/CNPJ with mask and remove mask when submit.
 *
 * @author Ayslan
 */
public class CpfCnpjConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String cpfCnpj = "";

        if (value != null) {
            cpfCnpj = value.replaceAll("[^\\d]", "");
        }

        return cpfCnpj;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String cpfCnpj = "";

        if (value != null && !value.toString().isEmpty()) {
            if (value.toString().length() > 11) {
                cpfCnpj = Mask.maskCnpj(value.toString());
            } else {
                cpfCnpj = Mask.maskCpf(value.toString());
            }
        }

        return cpfCnpj;
    }
}
