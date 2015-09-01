package com.xpert.faces.component.filter;

import java.util.List;
import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;

/**
 *
 * @author Ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js")
})
public class FilterOnEnter extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        target, selector;
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

    public String getTarget() {
        return (String) getStateHelper().eval(PropertyKeys.target, "");
    }

    public void setTarget(String target) {
        setAttribute(PropertyKeys.target, target);
    }

    public String getSelector() {
        return (String) getStateHelper().eval(PropertyKeys.selector, "");
    }

    public void setSelector(String selector) {
        setAttribute(PropertyKeys.selector, selector);
    }

    @SuppressWarnings("unchecked")
    public void setAttribute(final PropertyKeys property, final Object value) {
        getStateHelper().put(property, value);

        List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");

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
