package com.xpert.faces.conversion;

import com.xpert.core.conversion.Mask;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * Automotive Plate converter AAA-9999 format BR. Show a Automotive Plate with
 * mask and remove mask when submit
 *
 * @author Arnaldo
 */
public class PlacaCarroConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String placa = "";

        if (value != null) {
            placa = value.replaceAll("[^\\d]", "");
        }

        return placa;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String placa = "";

        if (value != null && !value.toString().isEmpty()) {
            placa = Mask.maskPlacaCarro(value.toString());
        }

        return placa;
    }
}
