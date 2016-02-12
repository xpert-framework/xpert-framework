package com.xpert.faces.component.restriction;

import com.xpert.faces.primefaces.LazyDataModelImpl;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import com.xpert.persistence.query.Restrictions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ComponentSystemEventListener;

public class RestrictionCollector implements ComponentSystemEventListener, StateHolder {

    private ValueExpression addTo;
    private ValueExpression property;
    private ValueExpression type;

    private boolean _transient;

    public RestrictionCollector() {
    }

    public RestrictionCollector(ValueExpression addTo, ValueExpression property, ValueExpression type) {
        this.addTo = addTo;
        this.property = property;
        this.type = type;
    }

    //implements ValueChangeListener
    
    // public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
//        if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
//            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
//            event.queue();
//            return;
//        }
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
//   Object value = event.getNewValue();
        Object value = ((EditableValueHolder) event.getComponent()).getValue();
       
        if (addTo != null) {

            RestrictionType restrictionType = null;
            //if no valu informed, then use "eq"
            if (type != null) {
                String restrictionTypeString = (String) type.getValue(elContext);
                //if a type is informed, then validate the type
                if (restrictionTypeString != null) {
                    restrictionType = RestrictionType.getByAcronym(restrictionTypeString);
                    if (restrictionType == null) {
                        throw new FacesException("Restriction type \"" + restrictionTypeString + "\" not found. The supported types are: " + RestrictionType.getAcronymList());
                    }

                }
            } else {
                restrictionType = RestrictionType.EQUALS;
            }

            Object addToValue = addTo.getValue(elContext);
            String propertyValue = (String) property.getValue(elContext);

            List<Restriction> restrictions = null;
            if (addToValue instanceof LazyDataModelImpl) {
                restrictions = ((LazyDataModelImpl) addToValue).getRestrictions();
                if (restrictions == null) {
                    restrictions = new Restrictions();
                    ((LazyDataModelImpl) addToValue).setRestrictions(restrictions);
                }
            } else if (addToValue instanceof List) {
                restrictions = (List) addToValue;
            }

            //if new value is empty then remove restriction
            if (isEmpty(value)) {
                //use iterator to remove
                Iterator<Restriction> itRestrictions = restrictions.iterator();
                while (itRestrictions.hasNext()) {
                    Restriction current = itRestrictions.next();
                    if (current.getProperty().equals(propertyValue)) {
                        itRestrictions.remove();
                        break;
                    }
                }
            } else {
                Restriction restriction = new Restriction();
                //if is an array convert it to List
                if (RestrictionType.IN.equals(restrictionType) && value != null && value instanceof Object[]) {
                    value = Arrays.asList((Object[]) value);
                }
                restriction.setValue(value);
                restriction.setRestrictionType(restrictionType);
                restriction.setProperty(propertyValue);
                if (restrictions != null) {
                    //verify unique
                    boolean found = false;
                    for (Restriction restr : restrictions) {
                        if (restr.getProperty().equals(propertyValue)) {
                            restr.setValue(restriction.getValue());
                            found = true;
                            break;
                        }
                    }
                    //add only if not found
                    if (found == false) {
                        restrictions.add(restriction);
                    }
                }
            }

        }
    }

    public boolean isEmpty(Object value) {

        if (value == null) {
            return true;
        }

        //if string verify if is empty
        if (value instanceof String && ((String) value).isEmpty()) {
            return true;
        }
        //if is array verifiy length
        if (value instanceof Object[] && ((Object[]) value).length == 0) {
            return true;
        }
        //if collection verify if is empty
        if (value instanceof Collection && ((Collection) value).isEmpty()) {
            return true;
        }

        return false;
    }

    public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = addTo;
        state[1] = property;
        state[2] = type;

        return state;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        addTo = (ValueExpression) values[0];
        property = (ValueExpression) values[1];
        type = (ValueExpression) values[2];
    }

    public boolean isTransient() {
        return _transient;
    }

    public void setTransient(boolean _transient) {
        this._transient = _transient;
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

}
