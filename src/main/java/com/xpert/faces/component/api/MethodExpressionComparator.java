package com.xpert.faces.component.api;

import java.util.Comparator;
import jakarta.el.MethodExpression;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class MethodExpressionComparator implements Comparator, Serializable {

    private static final long serialVersionUID = -4456370083061490865L;

    private FacesContext context;
    private MethodExpression methodExpression;
    private SortOrder sortOrder;

    public MethodExpressionComparator(FacesContext context, MethodExpression methodExpression, SortOrder sortOrder) {
        this.context = context;
        this.methodExpression = methodExpression;
        this.sortOrder = sortOrder;
    }

    public int compare(Object o1, Object o2) {
        if (sortOrder == null || sortOrder.equals(SortOrder.ASCENDING)) {
            return (Integer) methodExpression.invoke(context.getELContext(), new Object[]{o1, o2});
        } else {
            return (Integer) methodExpression.invoke(context.getELContext(), new Object[]{o2, o1});
        }
    }

    public FacesContext getContext() {
        return context;
    }

    public void setContext(FacesContext context) {
        this.context = context;
    }

    public MethodExpression getMethodExpression() {
        return methodExpression;
    }

    public void setMethodExpression(MethodExpression methodExpression) {
        this.methodExpression = methodExpression;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    
}
