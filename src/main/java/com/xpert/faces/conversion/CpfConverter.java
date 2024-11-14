package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import java.io.Serializable;

/**
 *
 * CPF converter. Show a CPF with mask and remove mask when submit
 *
 * @author ayslan
 */
public class CpfConverter implements Converter, Serializable {

    private static final long serialVersionUID = -6136878081485522910L;

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
