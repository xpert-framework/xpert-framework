package com.xpert.faces.component.restorablefilter;

import com.xpert.faces.utils.FacesUtils;
import java.util.List;
import java.util.Map;
import jakarta.el.ValueExpression;
import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js")
})
public class RestorableFilter extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        target;

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

    public static void storeFilterInSession(Map filters) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent dataTable = UIComponent.getCurrentComponent(context);
        FacesUtils.addToSession(dataTable.getClientId(), filters);
        context.getViewRoot().getViewMap().put(dataTable.getClientId() + "_restorableFilter", true);
    }

    public static void restoreFilterFromSession(Map currentFilters) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = UIComponent.getCurrentComponent(context);
        Map viewMap = context.getViewRoot().getViewMap();

        //only first time
        Object fromViewMap = viewMap.get(component.getClientId() + "_restorableFilter");
        if (fromViewMap != null) {
            return;
        }
        Map filters = (Map) FacesUtils.getFromSession(component.getClientId());
        if (component instanceof DataTable) {
            DataTable dataTable = (DataTable) component;
            if (filters != null && !filters.isEmpty()) {
                dataTable.setFilterByAsMap(filters);
            }
        }
        if (currentFilters != null && filters != null) {
            currentFilters.putAll(filters);
        }
    }

    public String getTarget() {
        return (String) getStateHelper().eval(RestorableFilter.PropertyKeys.target, null);
    }

    public void setTarget(final String target) {
        setAttribute(RestorableFilter.PropertyKeys.target, target);
    }

    @SuppressWarnings("unchecked")
    public void setAttribute(final RestorableFilter.PropertyKeys property, final Object value) {
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
