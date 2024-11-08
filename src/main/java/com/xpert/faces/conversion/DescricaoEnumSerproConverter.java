package com.xpert.faces.conversion;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 *
 * Converter to show Yes when true, No for false
 *
 * @author ayslan
 */
public class DescricaoEnumSerproConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String campo = (String) value;
        campo = campo.replace("_", " ");
        return campo;
    }
}
