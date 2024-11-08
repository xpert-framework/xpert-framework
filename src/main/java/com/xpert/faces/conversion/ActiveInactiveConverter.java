package com.xpert.faces.conversion;

import com.xpert.i18n.XpertResourceBundle;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 *
 * Converter to show Active when true and Inactive when false
 *
 * @author ayslan
 */
public class ActiveInactiveConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Boolean condition = (Boolean) value;
        if (condition) {
            return XpertResourceBundle.get("active");
        } else {
            return XpertResourceBundle.get("inactive");
        }
    }
}
