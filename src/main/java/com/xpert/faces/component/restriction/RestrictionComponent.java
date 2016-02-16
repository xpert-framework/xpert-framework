package com.xpert.faces.component.restriction;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

/**
 * This class is a java bean with some values to be used in restrictions
 * creation.
 *
 * @author Ayslan
 */
public class RestrictionComponent {

    private UIComponent component;
    private ValueExpression addTo;
    private ValueExpression property;
    private ValueExpression type;
    private ValueExpression ilike;
    private ValueExpression likeType;
    private ValueExpression temporalType;

    public RestrictionComponent() {
    }

    public RestrictionComponent(UIComponent component, ValueExpression addTo, ValueExpression property, ValueExpression type) {
        this.component = component;
        this.addTo = addTo;
        this.property = property;
        this.type = type;
    }

    public RestrictionComponent(UIComponent component, ValueExpression addTo, ValueExpression property, ValueExpression type, ValueExpression ilike, ValueExpression likeType, ValueExpression temporalType) {
        this.component = component;
        this.addTo = addTo;
        this.property = property;
        this.type = type;
        this.ilike = ilike;
        this.likeType = likeType;
        this.temporalType = temporalType;
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    public ValueExpression getAddTo() {
        return addTo;
    }

    public void setAddTo(ValueExpression addTo) {
        this.addTo = addTo;
    }

    public ValueExpression getProperty() {
        return property;
    }

    public void setProperty(ValueExpression property) {
        this.property = property;
    }

    public ValueExpression getType() {
        return type;
    }

    public void setType(ValueExpression type) {
        this.type = type;
    }

    public ValueExpression getIlike() {
        return ilike;
    }

    public void setIlike(ValueExpression ilike) {
        this.ilike = ilike;
    }

    public ValueExpression getLikeType() {
        return likeType;
    }

    public void setLikeType(ValueExpression likeType) {
        this.likeType = likeType;
    }

    public ValueExpression getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(ValueExpression temporalType) {
        this.temporalType = temporalType;
    }

}
