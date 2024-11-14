package com.xpert.faces.component.group;

import com.xpert.faces.component.api.MethodExpressionComparator;
import com.xpert.faces.component.api.UIData;
import com.xpert.faces.component.group.model.GroupModel;
import com.xpert.faces.component.api.SortOrder;
import java.util.Collections;
import java.util.List;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class Group extends UIData implements Serializable {

    private static final long serialVersionUID = -5131370014705617209L;
    
    public static final String COMPONENT_TYPE = "com.xpert.component.Group";
    public static final String COMPONENT_FAMILY = "com.xpert.component";
    private static final String DEFAULT_RENDERER = "com.xpert.fapces.component.GroupRenderer";

    private DataModel model = null;

    public Group() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    protected enum PropertyKeys {

        value, groupBy, itemSortField, itemSortOrder, sortOrder, sortField, rowIndexVar, sortFunction, itemSortFunction;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    /**
     * Override getDataModel to group the list
     *
     * @return
     */
    @Override
    protected DataModel getDataModel() {

        Object current = getValue();

        if (this.model != null) {
            return (model);
        }

        if (current == null) {
            model = new ListDataModel(Collections.EMPTY_LIST);
            return (model);
        }

        if (!(current instanceof List)) {
            throw new FacesException("Group Component supports only value of type List ");
        }

        GroupModel groupModel = new GroupModel(getGroupBy(), (List) current);
        groupModel.setSortField(getSortField());
        groupModel.setItemSortField(getItemSortField());

        String sortOrder = getSortOrder();
        if (sortOrder != null && !sortOrder.isEmpty()) {
            if (!sortOrder.toUpperCase().equals(SortOrder.ASCENDING.name())
                    && !sortOrder.toUpperCase().equals(SortOrder.DESCENDING.name())) {
                throw new FacesException("Sort Order must be \"ascending\" or \"descending\"");
            }
            groupModel.setSortOrder(SortOrder.valueOf(sortOrder.toUpperCase()));
        }

        String itemSortOrder = getItemSortOrder();
        if (itemSortOrder != null && !itemSortOrder.isEmpty()) {
            if (!itemSortOrder.toUpperCase().equals(SortOrder.ASCENDING.name())
                    && !itemSortOrder.toUpperCase().equals(SortOrder.DESCENDING.name())) {
                throw new FacesException("Item Sort Order must be \"ascending\" or \"descending\"");
            }
            groupModel.setItemSortOrder(SortOrder.valueOf(itemSortOrder.toUpperCase()));
        }

        MethodExpression sortFunction = getSortFunction();
        if (sortFunction != null) {
            groupModel.setComparator(new MethodExpressionComparator(this.getFacesContext(), sortFunction, groupModel.getSortOrder()));
        }
        MethodExpression itemSortFunction = getItemSortFunction();
        if (itemSortFunction != null) {
            groupModel.setItemComparator(new MethodExpressionComparator(this.getFacesContext(), itemSortFunction, groupModel.getItemSortOrder()));
        }

        groupModel.groupItens();

        model = new ListDataModel(groupModel.getItens());
//        requestMap.put("dataModel_" + getClientId(), model);
        setDataModel(model);

        return model;
    }

    @Override
    protected void setDataModel(DataModel dataModel) {
        this.model = dataModel;
    }

    public String getSortField() {
        return (String) getStateHelper().eval(PropertyKeys.sortField, "");
    }

    public void setSortField(String field) {
        setAttribute(PropertyKeys.sortField, field);
    }

    public String getSortOrder() {
        return (String) getStateHelper().eval(PropertyKeys.sortOrder, "");
    }

    public void setSortOrder(String field) {
        setAttribute(PropertyKeys.sortOrder, field);
    }

    public String getItemSortField() {
        return (String) getStateHelper().eval(PropertyKeys.itemSortField, "");
    }

    public void setItemSortField(String field) {
        setAttribute(PropertyKeys.itemSortField, field);
    }

    public String getItemSortOrder() {
        return (String) getStateHelper().eval(PropertyKeys.itemSortOrder, null);
    }

    public void setItemSortOrder(String field) {
        setAttribute(PropertyKeys.itemSortOrder, field);
    }

    public String getGroupBy() {
        return (String) getStateHelper().eval(PropertyKeys.groupBy, "");
    }

    public void setGroupBy(String field) {
        setAttribute(PropertyKeys.groupBy, field);
    }

    public String getRowIndexVar() {
        return (String) getStateHelper().eval(PropertyKeys.rowIndexVar, null);
    }

    public void setRowIndexVar(String rowIndexVar) {
        setAttribute(PropertyKeys.rowIndexVar, rowIndexVar);
    }

    public MethodExpression getSortFunction() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.sortFunction, null);
    }

    public void setSortFunction(MethodExpression _sortFunction) {
        getStateHelper().put(PropertyKeys.sortFunction, _sortFunction);
    }

    public MethodExpression getItemSortFunction() {
        return (MethodExpression) getStateHelper().eval(PropertyKeys.itemSortFunction, null);
    }

    public void setItemSortFunction(MethodExpression _itemSortFunction) {
        getStateHelper().put(PropertyKeys.itemSortFunction, _itemSortFunction);
    }

    @SuppressWarnings("unchecked")
    public void setAttribute(final Group.PropertyKeys property, final Object value) {

        getStateHelper().put(property, value);

        List<String> setAttributes = (List<String>) this.getAttributes().get("jakarta.faces.component.UIComponentBase.attributesThatAreSet");

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
