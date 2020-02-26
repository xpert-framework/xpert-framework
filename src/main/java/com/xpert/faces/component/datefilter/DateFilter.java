package com.xpert.faces.component.datefilter;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.html.HtmlPanelGroup;

/**
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "css/style.css")
    ,
    @ResourceDependency(library = "xpert", name = "scripts/core.js")
})
public class DateFilter extends HtmlPanelGroup {

    private static final String COMPONENT_FAMILY = "com.xpert.component";
    private boolean added;
    private Object calendarStartValue;
    private Object calendarEndValue;

    public DateFilter() {
        //The default constructor does nothing
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    protected enum PropertyKeys {

        value;
        String toString;

        PropertyKeys(String toString) {
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

    public String getValue() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.value);
    }

    public void setValue(String _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public Object getCalendarStartValue() {
        return calendarStartValue;
    }

    public void setCalendarStartValue(Object calendarStartValue) {
        this.calendarStartValue = calendarStartValue;
    }

    public Object getCalendarEndValue() {
        return calendarEndValue;
    }

    public void setCalendarEndValue(Object calendarEndValue) {
        this.calendarEndValue = calendarEndValue;
    }
}
