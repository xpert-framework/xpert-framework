package com.xpert.faces.component.restriction;

import com.xpert.faces.primefaces.LazyDataModelImpl;
import static com.xpert.faces.utils.FacesUtils.findComponent;
import com.xpert.persistence.query.LikeType;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.RestrictionType;
import com.xpert.persistence.query.Restrictions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.persistence.TemporalType;

public class RestrictionCollector implements ActionListener, StateHolder {

    private static final Logger logger = Logger.getLogger(RestrictionCollector.class.getName());

    private ValueExpression addTo;
    private ValueExpression debug;
    private boolean _transient;

    public RestrictionCollector() {
    }

    public RestrictionCollector(ValueExpression addTo) {
        this.addTo = addTo;
    }

    public RestrictionCollector(ValueExpression addTo, ValueExpression debug) {
        this.addTo = addTo;
        this.debug = debug;
    }

    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {

        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        List<RestrictionComponent> currentRestrictions = (List<RestrictionComponent>) requestMap.get(RestrictionComponent.class.getName());
        if (currentRestrictions != null) {
            for (RestrictionComponent component : currentRestrictions) {
                addRestriction(component);
            }
        }
    }

    public void addRestriction(RestrictionComponent restrictionComponent) throws AbortProcessingException {
        addRestriction(restrictionComponent.getComponent(), restrictionComponent.getAddTo(), restrictionComponent.getProperty(), restrictionComponent.getType(),
                restrictionComponent.getIlike(), restrictionComponent.getLikeType(), restrictionComponent.getTemporalType());

    }

    public void addRestriction(UIComponent component, ValueExpression addTo, ValueExpression property, ValueExpression type, ValueExpression ilike, ValueExpression likeType, ValueExpression temporalType) throws AbortProcessingException {

        //use collector addTo if component is null
        if (addTo == null) {
            addTo = this.addTo;
        }

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();
        Object value = null;
        if (component instanceof EditableValueHolder) {
            value = ((EditableValueHolder) component).getValue();
        } else {
            //try to add in children
            Iterator children = component.getFacetsAndChildren();
            while (children.hasNext()) {
                UIComponent child = (UIComponent) children.next();
               // System.out.println(child);
                addRestriction(child, addTo, property, type, ilike, likeType, temporalType);
            }
            return;
        }

        Object debugValue = (debug != null) ? debug.getValue(elContext) : null;
        boolean checkDebug = (debugValue == null) ? false : (Boolean.valueOf(debugValue.toString()));

        if (addTo != null) {

            RestrictionType restrictionType = null;
            //if no valu informed, then use "eq"
            if (type != null) {
                String restrictionTypeString = (String) type.getValue(elContext);
                //if a type is informed, then validate the type
                if (restrictionTypeString != null) {
                    restrictionType = RestrictionType.getByAcronym(restrictionTypeString);
                }
            } else {
                restrictionType = RestrictionType.EQUALS;
            }

            boolean ilikeValue = true;
            //if no valu informed, then use "eq"
            if (ilike != null) {
                ilikeValue = Boolean.valueOf(ilike.toString());
            }

            LikeType likeTypeValue = null;
            //if no valu informed, then use "eq"
            if (likeType != null) {
                //get from enum
                likeTypeValue = LikeType.valueOf(likeType.getValue(elContext).toString().toUpperCase());
            } else {
                //default value
                likeTypeValue = LikeType.BOTH;
            }

            TemporalType temporalTypeValue = null;
            //if no valu informed, then use "eq"
            if (temporalType != null) {
                //get from enum
                temporalTypeValue = TemporalType.valueOf(temporalType.getValue(elContext).toString().toUpperCase());
            }

            Object addToValue = addTo.getValue(elContext);
            String propertyValue = (String) property.getValue(elContext);

            List<Restriction> restrictions = null;
            if (addToValue instanceof LazyDataModelImpl) {
                ((LazyDataModelImpl) addToValue).setDebug(checkDebug);
                restrictions = ((LazyDataModelImpl) addToValue).getRestrictions();
                if (restrictions == null) {
                    restrictions = new Restrictions();
                    ((LazyDataModelImpl) addToValue).setRestrictions(restrictions);
                }
            } else if (addToValue instanceof List) {
                restrictions = (List) addToValue;
            }
            
            System.out.println(propertyValue+" - "+restrictionType);

            //if new value is empty then remove restriction
            if (isEmpty(value)) {
                //use iterator to remove
                Iterator<Restriction> itRestrictions = restrictions.iterator();
                while (itRestrictions.hasNext()) {
                    Restriction current = itRestrictions.next();
                    if (current.getProperty().equals(propertyValue) && current.getRestrictionType().equals(restrictionType)) {
                        if (checkDebug) {
                            logger.log(Level.INFO, "Restriction removed: {0}", current);
                        }
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
                restriction.setIlike(ilikeValue);
                restriction.setLikeType(likeTypeValue);
                restriction.setTemporalType(temporalTypeValue);

                if (checkDebug) {
                    logger.log(Level.INFO, "Restriction added: {0}", restriction);
                }

                if (restrictions != null) {
                    //verify unique
                    boolean found = false;
                    for (Restriction restr : restrictions) {
                        if (restr.getProperty().equals(propertyValue) && restr.getRestrictionType().equals(restrictionType)) {
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
        state[1] = debug;

        return state;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        addTo = (ValueExpression) values[0];
        debug = (ValueExpression) values[1];
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

    public ValueExpression getDebug() {
        return debug;
    }

    public void setDebug(ValueExpression debug) {
        this.debug = debug;
    }

}
