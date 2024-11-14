package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import java.io.Serializable;

/**
 *
 * CPF/CNPJ converter. Show a CPF/CNPJ with mask and remove mask when submit.
 *
 * @author ayslan
 */
public class CpfCnpjConverter implements Converter, Serializable {

    private static final long serialVersionUID = 1957481896264083424L;

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
