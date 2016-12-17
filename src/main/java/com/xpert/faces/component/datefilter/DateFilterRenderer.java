package com.xpert.faces.component.datefilter;

import com.xpert.faces.primefaces.PrimeFacesUtils;
import com.xpert.i18n.I18N;
import com.xpert.i18n.XpertResourceBundle;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.calendar.CalendarRenderer;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author ayslan
 */
public class DateFilterRenderer extends Renderer {

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        setDateFilterValues(context, component);
    }

    public void setDateFilterValues(FacesContext context, UIComponent component) {
        DateFilter dateFilter = (DateFilter) component;

        String clientId = dateFilter.getClientId(context);
        String startCalendarValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_calendar-start_input");
        String endCalendarValue = context.getExternalContext().getRequestParameterMap().get(dateFilter.getId() + "_calendar-end_input");

        if (startCalendarValue != null) {
            dateFilter.setCalendarStartValue(startCalendarValue);
        }
        if (endCalendarValue != null) {
            dateFilter.setCalendarEndValue(endCalendarValue);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        setDateFilterValues(context, component);
        encodeDateFilter(component, context);
    }

    public UIComponent getColumnParent(UIComponent component) {
        if (component != null) {
            UIComponent parent = component.getParent();
            if (parent instanceof Column) {
                return parent;
            } else {
                if (component.getParent() != null) {
                    return getColumnParent(component.getParent());
                }
            }
        }
        return null;
    }

    public void encodeDateFilter(UIComponent component, FacesContext context) throws IOException {

        DateFilter dateFilter = (DateFilter) component;
        String dateFormat = I18N.getDatePattern();

        Calendar calendarStart = new Calendar();
        calendarStart.setStyleClass("calendar-filter calendar-filter-start");
        calendarStart.setNavigator(true);
        calendarStart.setShowOn("both");
        calendarStart.setShowButtonPanel(true);
        calendarStart.setPattern(dateFormat);
        calendarStart.setReadonly(true);
        calendarStart.setId(component.getId() + "_calendar-start");

        if (dateFilter.getCalendarStartValue() != null) {
            calendarStart.setSubmittedValue(dateFilter.getCalendarStartValue());
        }

        Calendar calendarEnd = new Calendar();
        calendarEnd.setStyleClass("calendar-filter calendar-filter-end");
        calendarEnd.setNavigator(true);
        calendarEnd.setShowOn("both");
        calendarEnd.setShowButtonPanel(true);
        calendarEnd.setPattern(dateFormat);
        calendarEnd.setReadonly(true);
        calendarEnd.setId(component.getId() + "_calendar-end");

        if (dateFilter.getCalendarEndValue() != null) {
            calendarEnd.setSubmittedValue(dateFilter.getCalendarEndValue());
        }

        Column column = (Column) getColumnParent(component);
        if (column == null) {
            throw new FacesException("Date Filter must be child of a Column");
        }

        column.setFilterStyle("display: none;");
        DataTable dataTable = (DataTable) column.getParent();
        String widgetVar = dataTable.resolveWidgetVar();

        if (!PrimeFacesUtils.isVersion3()) {
            widgetVar = "PF('" + widgetVar + "')";
        }

        String filterScript = "Xpert.dateFilter('" + column.getClientId() + "');" + widgetVar + ".filter(); return false;";

        ClientBehavior clientBehavior = createClientBehavior(filterScript);

        calendarStart.addClientBehavior("dateSelect", clientBehavior);
        calendarEnd.addClientBehavior("dateSelect", clientBehavior);

        calendarStart.setParent(component);
        calendarEnd.setParent(component);

        //writer response
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", component);
        writer.writeAttribute("class", "panel-calendar-filter", null);
        writer.startElement("table", component);
        //first line
        writer.startElement("tr", component);
        //first column
        writer.startElement("td", component);
        writer.write(XpertResourceBundle.get("from"));
        writer.endElement("td");
        //second column
        writer.startElement("td", component);

        CalendarRenderer calendarRenderer = new CalendarRenderer();
        calendarRenderer.encodeEnd(context, calendarStart);

        writer.endElement("td");
        writer.endElement("tr");
        //second line
        writer.startElement("tr", component);
        //first column
        writer.startElement("td", component);
        writer.write(XpertResourceBundle.get("to"));
        writer.endElement("td");
        //second column

        writer.startElement("td", component);
        calendarRenderer = new CalendarRenderer();
        calendarRenderer.encodeEnd(context, calendarEnd);
        writer.endElement("td");
        writer.endElement("tr");
        writer.endElement("table");
        writer.startElement("a", component);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("onclick", "Xpert.clearDateFilter(this);" + widgetVar + ".filter();", null);
        writer.write(XpertResourceBundle.get("clear"));
        writer.endElement("a");

        //script to init date filter (corret bugs in primefaces DOM manipulation like reflow)
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("Xpert.initDateFilter('" + column.getClientId() + "');");
        writer.endElement("script");

        writer.endElement("div");
        //clear parent and prevent to add to tree
        calendarStart.setParent(null);
        calendarEnd.setParent(null);

    }

    /**
     * Hack to create PrimeFaces ajax behavior.
     * <ul>
     * <li>Primefaces 3 and 4:
     * org.primefaces.component.behavior.ajax.AjaxBehavior</li>
     * <li>Primefaces 5: org.primefaces.behavior.ajax.AjaxBehavior</li>
     * </ul>
     *
     * @param scriptOnStart
     * @return
     */
    public ClientBehavior createClientBehavior(String scriptOnStart) {
        ClientBehavior clientBehavior = null;

        if (AJAX_BEHAVIOR_CLASS == null) {
            try {
                AJAX_BEHAVIOR_CLASS = Class.forName(AJAX_BEHAVIOR_CLASS_PRIMEFACES_3_AND_4);
            } catch (ClassNotFoundException ex) {
                try {
                    AJAX_BEHAVIOR_CLASS = Class.forName(AJAX_BEHAVIOR_CLASS_PRIMEFACES_5);
                } catch (ClassNotFoundException ex1) {
                    throw new RuntimeException(ex1);
                }
            }
        }

        try {
            if (SET_ON_START_METHOD == null) {
                SET_ON_START_METHOD = AJAX_BEHAVIOR_CLASS.getMethod(SET_ON_START_METHOD_NAME, String.class);
            }
            clientBehavior = (ClientBehavior) AJAX_BEHAVIOR_CLASS.newInstance();
            SET_ON_START_METHOD.invoke(clientBehavior, scriptOnStart);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return clientBehavior;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public static final String AJAX_BEHAVIOR_CLASS_PRIMEFACES_3_AND_4 = "org.primefaces.component.behavior.ajax.AjaxBehavior";
    public static final String AJAX_BEHAVIOR_CLASS_PRIMEFACES_5 = "org.primefaces.behavior.ajax.AjaxBehavior";
    public static final String SET_ON_START_METHOD_NAME = "setOnstart";
    public static Method SET_ON_START_METHOD = null;
    public static Class AJAX_BEHAVIOR_CLASS = null;

}
