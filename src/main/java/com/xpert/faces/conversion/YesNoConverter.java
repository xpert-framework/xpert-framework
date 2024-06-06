package com.xpert.faces.conversion;

import com.xpert.i18n.XpertResourceBundle;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 *
 * Converter to show Yes when true, No for false
 *
 * @author ayslan
 */
public class YesNoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
       return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Boolean condition = (Boolean) value;
        if (condition) {
            return XpertResourceBundle.get("yes");
        } else {
            return XpertResourceBundle.get("no");
        }
    }
}
