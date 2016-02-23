package com.xpert.faces.component.confirmation;

import java.util.List;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;

/**
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "css/style.css"),
    @ResourceDependency(library = "xpert", name = "scripts/core.js")
})
public class Confirmation extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        message,
        confirmLabel,
        cancelLabel;
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

    public String getConfirmLabel() {
        return (String) getStateHelper().eval(PropertyKeys.confirmLabel, null);
    }

    public void setConfirmLabel(final String confirmLabel) {
        setAttribute(PropertyKeys.confirmLabel, confirmLabel);
    }
    
    public String getCancelLabel() {
        return (String) getStateHelper().eval(PropertyKeys.cancelLabel, null);
    }

    public void setCancelLabel(final String cancelLabel) {
        setAttribute(PropertyKeys.cancelLabel, cancelLabel);
    }
    
    public String getMessage() {
        return (String) getStateHelper().eval(PropertyKeys.message, null);
    }

    public void setMessage(final String message) {
        setAttribute(PropertyKeys.message, message);
    }

    @SuppressWarnings("unchecked")
    public void setAttribute(final PropertyKeys property, final Object value) {
        getStateHelper().put(property, value);

        List<String> setAttributes =
                (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");

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
