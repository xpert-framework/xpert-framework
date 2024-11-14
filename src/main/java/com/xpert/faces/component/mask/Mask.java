package com.xpert.faces.component.mask;

import java.util.List;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponentBase;
import java.io.Serializable;

/**
 * This component puts a mask in primefaces p:calendar (in previous version
 * primefaces doesn't has mask on calendar)
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js")
})
public class Mask extends UIComponentBase implements Serializable {

    private static final long serialVersionUID = 8212241760252181231L;

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        placeHolder;

        private String toString;

        PropertyKeys(final String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceHolder() {
        return (String) getStateHelper().eval(Mask.PropertyKeys.placeHolder, null);
    }

    public void setPlaceHolder(final String confirmLabel) {
        setAttribute(Mask.PropertyKeys.placeHolder, confirmLabel);
    }

    @SuppressWarnings("unchecked")
    public void setAttribute(final Mask.PropertyKeys property, final Object value) {
        getStateHelper().put(property, value);

        List<String> setAttributes
                = (List<String>) this.getAttributes().get("jakarta.faces.component.UIComponentBase.attributesThatAreSet");

        if (setAttributes != null && value == null) {
            final String attributeName = property.toString();
            final ValueExpression ve = getValueExpression(attributeName);
            if (ve == null) {
                setAttributes.remove(attributeName);
            } else if (!setAttributes.contains(attributeName)) {
                setAttributes.add(attributeName);
            }
        }
    }
}
