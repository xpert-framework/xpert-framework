package com.xpert.faces.conversion;

import com.xpert.persistence.utils.EntityUtils;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;

/**
 *
 * @author ayslan
 */
public class EntityConverterList implements Converter {

    private static final Logger logger = Logger.getLogger(EntityConverterList.class.getName());
    private static final boolean DEBUG = false;

    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        Object list = component.getAttributes().get("list");
        if (list == null) {
            logger.log(Level.WARNING, "Attribute list not defined in component. Set <f:attribute name=\"list\" value=\"#{yourList}\" /> in component ");
            return null;
        }
        if (list instanceof Collection == false) {
            logger.log(Level.WARNING, "Attribute list must be a instanceof java.util.Collection, found: {0}", list.getClass().getName());
            return null;
        }
        for (Object object : (Collection) list) {
            Object id = EntityUtils.getId(object);
            if (id != null && id.toString() != null && id.toString().equals(value)) {
                if (DEBUG) {
                    logger.log(Level.INFO, "Found object in collection, id: {0}", id);
                }
                return object;
            }
        }

        if (DEBUG) {
            logger.log(Level.INFO, "Id {0} not found in collection", value);
        }

        return null;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            Object id = EntityUtils.getId(value);
            if (id != null) {
                if (DEBUG) {
                    logger.log(Level.INFO, "Method getAsString(). Id {0} found in object", id);
                }
                return id.toString();
            }
        }
        return null;
    }

}
