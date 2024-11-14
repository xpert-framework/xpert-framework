package com.xpert.faces.component.restriction;

import com.xpert.persistence.query.LikeType;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import java.util.Arrays;
import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.UIComponent;
import jakarta.persistence.TemporalType;
import java.io.Serializable;

/**
 * This class is a java bean with some values to be used in restrictions
 * creation.
 *
 * @author ayslan
 */
public class RestrictionComponent implements Serializable {

    private static final long serialVersionUID = -5748538755874358960L;

    private UIComponent component;
    private ValueExpression addTo;
    private ValueExpression property;
    private ValueExpression type;
    private ValueExpression ilike;
    private ValueExpression likeType;
    private ValueExpression temporalType;
    private ValueExpression rendered;
    private ValueExpression castAs;

    public RestrictionComponent() {
    }

    public RestrictionComponent(UIComponent component, ValueExpression addTo, ValueExpression property, ValueExpression type) {
        this.component = component;
        this.addTo = addTo;
        this.property = property;
        this.type = type;
    }

    public RestrictionComponent(UIComponent component, ValueExpression addTo, ValueExpression property, ValueExpression type, ValueExpression ilike,
            ValueExpression likeType, ValueExpression temporalType, ValueExpression rendered, ValueExpression castAs) {
        this.component = component;
        this.addTo = addTo;
        this.property = property;
        this.type = type;
        this.ilike = ilike;
        this.likeType = likeType;
        this.temporalType = temporalType;
        this.rendered = rendered;
        this.castAs = castAs;
    }

    public boolean isRendered(ELContext elContext) {
        Object renderedValue = (getRendered() != null) ? getRendered().getValue(elContext) : null;
        return (renderedValue == null) ? true : (Boolean.valueOf(renderedValue.toString()));
    }

    /**
     * Convert a instance of RestrictionComponent to Restriction
     *
     * @param elContext
     * @param component
     * @return
     */
    public Restriction toRestriction(ELContext elContext, UIComponent component) {

        Restriction restriction = new Restriction();
        RestrictionType restrictionType = null;
        //if no value informed, then use "eq"
        if (type != null) {
            String restrictionTypeString = (String) type.getValue(elContext);
            //if a type is informed, then validate the type
            if (restrictionTypeString != null) {
                restrictionType = RestrictionType.getByAcronym(restrictionTypeString);
            }
        } else {
            restrictionType = RestrictionType.EQUALS;
        }

        Boolean ilikeValue = true;
        //if no value informed, then use "eq"
        if (ilike != null) {
            ilikeValue = (Boolean) ilike.getValue(elContext);
        }

        LikeType likeTypeValue = null;
        //if no valuee informed, then use "both"
        if (likeType != null) {
            //get from enum
            likeTypeValue = LikeType.valueOf(likeType.getValue(elContext).toString().toUpperCase());
        } else {
            //default value
            likeTypeValue = LikeType.BOTH;
        }

        TemporalType temporalTypeValue = null;
        //if no value informed, then use "null"
        if (temporalType != null) {
            //get from enum
            temporalTypeValue = TemporalType.valueOf(temporalType.getValue(elContext).toString().toUpperCase());
        }

        String castAsValue = null;
        if (castAs != null) {
            castAsValue = (String) castAs.getValue(elContext);
        }

        Object value = ((EditableValueHolder) component).getValue();
        //Hibernate do not accpet Object[] (arrays), so convert it to List
        if (RestrictionType.IN.equals(restrictionType) && value != null && value instanceof Object[]) {
            value = Arrays.asList((Object[]) value);
        }
        restriction.setValue(value);

        restriction.setRestrictionType(restrictionType);
        restriction.setProperty((String) property.getValue(elContext));
        restriction.setIlike(ilikeValue);
        restriction.setLikeType(likeTypeValue);
        restriction.setTemporalType(temporalTypeValue);
        restriction.setComponentId(component.getId());
        restriction.setCastAs(castAsValue);

        return restriction;
    }

    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

    public ValueExpression getCastAs() {
        return castAs;
    }

    public void setCastAs(ValueExpression castAs) {
        this.castAs = castAs;
    }

    public ValueExpression getRendered() {
        return rendered;
    }

    public void setRendered(ValueExpression rendered) {
        this.rendered = rendered;
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
