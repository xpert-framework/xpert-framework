package com.xpert.faces.component.restorablefilter;

import com.xpert.faces.component.datefilter.DateFilter;
import com.xpert.faces.primefaces.LazyDataModelImpl;
import com.xpert.faces.utils.FacesUtils;
import com.xpert.persistence.query.RestrictionsNormalizer;
import com.xpert.utils.StringEscapeUtils;
import java.io.IOException;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author ayslan
 */
public class RestorableFilterRenderer extends Renderer {

    @Override
    public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
        final RestorableFilter restorableFilter = (RestorableFilter) component;
        UIComponent targetComponent = component.findComponent(restorableFilter.getTarget());
        if (targetComponent == null) {
            throw new FacesException("Cannot find component " + restorableFilter.getTarget() + " in view.");
        } else {
            if (targetComponent instanceof DataTable == false) {
                throw new FacesException("RestorableFilter target " + restorableFilter.getTarget() + " (" + targetComponent.getClass().getName() + ")" + " is not a DataTable");
            }
            final ResponseWriter writer = context.getResponseWriter();
            final DataTable dataTable = (DataTable) targetComponent;
            ValueExpression modelVE = dataTable.getValueExpression("value");
            if (modelVE != null) {
                Object model = modelVE.getValue(context.getELContext());
                if (model instanceof LazyDataModelImpl) {
                    ((LazyDataModelImpl) model).setRestorableFilter(true);
                } else {
                    throw new FacesException("DataTable " + restorableFilter.getTarget() +"(" +model.getClass().getName()+") is not a instance of " + LazyDataModelImpl.class.getName());
                }
            }
            String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
            StringBuilder bodyScript = new StringBuilder();
            Map filters = (Map) FacesUtils.getFromSession(dataTable.getClientId());
            if (filters != null) {
                for (UIColumn uicolumn : dataTable.getColumns()) {
                    // params.put("teste", "teste");
                    Column column = (Column) uicolumn;
                    ValueExpression valueExpressionFilterBy = column.getValueExpression("filterBy");
                    if (valueExpressionFilterBy != null) {
                        String expressionString = valueExpressionFilterBy.getExpressionString();
                        expressionString = expressionString.substring(2, expressionString.length() - 1);      //Remove #{}
                        if (expressionString.indexOf(".") > 0) {
                            expressionString = expressionString.substring(expressionString.indexOf(".") + 1, expressionString.length());      //Remove first property.
                        }
                        if (filters.containsKey(expressionString)) {
                            ValueExpression valueExpression = column.getValueExpression("filterValue");
                            if (valueExpression != null) {
                                valueExpression.setValue(context.getELContext(), filters.get(expressionString));
                            } else {
                                String columnId = column.getContainerClientId(context);
                                String filterId = columnId + separator + "filter";
                                Object filterValue = filters.get(expressionString);
                                if (filterValue != null) {
                                    String escapedValue = StringEscapeUtils.escapeJavaScript(filterValue.toString());
                                    bodyScript.append("$(PrimeFaces.escapeClientId('").append(filterId).append("')).val('").append(escapedValue).append("');");
                                    UIComponent header = column.getFacet("header");
                                    if (header != null) {
                                        for (UIComponent child : header.getChildren()) {
                                            if (child instanceof DateFilter) {
                                                String[] dates = filterValue.toString().split(RestrictionsNormalizer.DATE_FILTER_INTERVAL_SEPARATOR);
                                                String start = "";
                                                if (dates.length > 0) {
                                                    start = dates[0];
                                                }
                                                String end = "";
                                                if (dates.length > 1) {
                                                    end = dates[1];
                                                }
                                                bodyScript.append("Xpert.refreshDateFilter('").append(columnId).append("', '").append(start).append("','").append(end).append("');");
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            StringBuilder scriptBuilder = new StringBuilder();
            if (bodyScript.length() > 0) {
                scriptBuilder.append("$(function(){");
                scriptBuilder.append(bodyScript);
                scriptBuilder.append("});");
            }

            String scriptFilters = scriptBuilder.toString();

            if (scriptFilters != null && !scriptFilters.isEmpty()) {
                writer.startElement("script", null);
                writer.write(scriptFilters);
                writer.endElement("script");
            }
        }
    }

}
