package com.xpert.faces.component.spread;

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
public class Spread extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        columns, highlight;
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

    public String getColumns() {
        return (String) getStateHelper().eval(PropertyKeys.columns, 1);
    }

    public void setColumns(String columns) {
        setAttribute(PropertyKeys.columns, columns);
    }
    public boolean getHighlight() {
        return (Boolean) getStateHelper().eval(PropertyKeys.highlight, true);
    }

    public void setHighlight(boolean highlight) {
        setAttribute(PropertyKeys.highlight, highlight);
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
