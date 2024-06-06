package com.xpert.faces.component.restriction;

import com.xpert.faces.primefaces.LazyDataModelImpl;
import com.xpert.persistence.query.Restriction;
import com.xpert.persistence.query.Restrictions;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.StateHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

/**
 * This class is a ActionListener used to add restrictions to a List or a
 * LazyDataModel
 *
 * @author ayslan
 */
public class RestrictionCollector implements ActionListener, StateHolder {

    private static final Logger logger = Logger.getLogger(RestrictionCollector.class.getName());
    public static final String IGNORE_RESTRICTIONS = RestrictionCollector.class.getName() + "_ignoreCurrentRestrictions";
    public static final String RESTRICTIONS = RestrictionCollector.class.getName() + "_restrictions";

    private ValueExpression addTo;
    private ValueExpression debug;
    private boolean _transient;
    private List<Restriction> currentRestrictions;

    public RestrictionCollector() {
    }

    public RestrictionCollector(ValueExpression addTo) {
        this.addTo = addTo;
    }

    public RestrictionCollector(ValueExpression addTo, ValueExpression debug) {
        this.addTo = addTo;
        this.debug = debug;
    }

    /**
     * Define if restrictions will be ignored in current request
     */
    public static void ignoreRestrictions() {
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        requestMap.put(IGNORE_RESTRICTIONS, true);
    }

    /**
     * Return true if restrictions will be ignored in current request
     *
     * @return true if restrictions will be ignored in current request
     */
    public static boolean isIgnoreRestrictions() {
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        Object value = requestMap.get(IGNORE_RESTRICTIONS);
        return value != null && (Boolean) value == true;
    }

    /**
     * Return true if restrictions will be ignored in current request
     *
     * @param context Current faces context
     * @return true if restrictions will be ignored in current request
     */
    public static boolean isIgnoreRestrictions(FacesContext context) {
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        Object value = requestMap.get(IGNORE_RESTRICTIONS);
        return value != null && (Boolean) value == true;
    }

    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {

        FacesContext context = FacesContext.getCurrentInstance();
        if (isIgnoreRestrictions(context)) {
            return;
        }

        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        List<RestrictionComponent> currentRequestRestrictions = (List<RestrictionComponent>) requestMap.get(RESTRICTIONS);
        if (currentRequestRestrictions != null) {
            int index = 0;
            currentRestrictions = null;
            for (RestrictionComponent restrictionComponent : currentRequestRestrictions) {
                addRestriction(restrictionComponent.getComponent(), restrictionComponent, context, index);
                index++;
            }
            if (currentRestrictions != null) {
                Collections.sort(currentRestrictions);
            }
        }

    }

    /**
     * Returns the current list of restrictions in request map. If no
     * restriction is found, the return a empty list.
     *
     * @return list of restrictions in request map
     */
    public static List<Restriction> getCurrentRestrictions() {
        return getCurrentRestrictions(FacesContext.getCurrentInstance());
    }

    /**
     * Returns the current list of restrictions in request map. If no
     * restriction is found, the return a empty list.
     *
     * @param context Current FacesContext
     * @return list of restrictions in request map
     */
    public static List<Restriction> getCurrentRestrictions(FacesContext context) {
        ELContext elContext = context.getELContext();
        Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
        List<RestrictionComponent> currentRestrictions = (List<RestrictionComponent>) requestMap.get(RESTRICTIONS);
        Restrictions restrictions = new Restrictions();
        if (currentRestrictions != null) {
            for (RestrictionComponent restrictionComponent : currentRestrictions) {
                Restriction restriction = restrictionComponent.toRestriction(elContext, restrictionComponent.getComponent());
                if (restrictionComponent.isRendered(elContext) && !isEmpty(restriction.getValue())) {
                    restrictions.add(restriction);
                }
            }
        }
        return restrictions;
    }

    public boolean isExecuteComponent(FacesContext context, UIComponent component) {
        boolean processComponent = false;
        if (context.getPartialViewContext().isAjaxRequest()) {
            Collection<String> executeIds = context.getPartialViewContext().getExecuteIds();
            if (executeIds != null) {
                for (String execute : executeIds) {
                    UIComponent root = context.getViewRoot().findComponent(execute);
                    if (root != null) {
                        if (isChild(root, component)) {
                            processComponent = true;
                        }
                    }
                }
            }

        } else {
            //non-ajax just verify if is in parameters map
            processComponent = context.getExternalContext().getRequestParameterValuesMap().containsKey(component.getClientId());
        }
        return processComponent;
    }

    public boolean isChild(UIComponent base, UIComponent child) {

        if (base.equals(child)) {
            return true;
        }

        if (base.getFacetsAndChildren() != null) {
            Iterator children = base.getFacetsAndChildren();
            while (children.hasNext()) {
                UIComponent current = (UIComponent) children.next();
                if (current.equals(child) || isChild(current, child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void addRestriction(UIComponent component, RestrictionComponent restrictionComponent, FacesContext context, int index) throws AbortProcessingException {

        //if not EditableValueHolder try to add child
        if (!(component instanceof EditableValueHolder)) {
            //try to add in children
            Iterator children = component.getFacetsAndChildren();
            while (children.hasNext()) {
                UIComponent child = (UIComponent) children.next();
                // System.out.println(child);
                addRestriction(child, restrictionComponent, context, index);
            }
            return;
        }

        ELContext elContext = context.getELContext();

        Object debugValue = (debug != null) ? debug.getValue(elContext) : null;
        boolean checkDebug = (debugValue == null) ? false : (Boolean.valueOf(debugValue.toString()));

        if (!isExecuteComponent(context, component)) {
            //logger.log(Level.INFO, "Component: {0} is ignored from restrictions", new Object[]{component.getClientId()});
            return;
        }

        ValueExpression addTo = restrictionComponent.getAddTo();

        //use collector addTo if component is null
        if (addTo == null) {
            addTo = this.addTo;
        }

        if (addTo != null) {

            Object addToValue = addTo.getValue(elContext);

            List<Restriction> restrictions = null;
            if (addToValue instanceof LazyDataModelImpl) {
                LazyDataModelImpl dataModel = (LazyDataModelImpl) addToValue;
                dataModel.setDebug(checkDebug);
                //force load data
                dataModel.setLoadData(true);
                restrictions = dataModel.getRestrictions();
                if (restrictions == null) {
                    restrictions = new Restrictions();
                    dataModel.setRestrictions(restrictions);
                }
            } else if (addToValue instanceof List) {
                restrictions = (List) addToValue;
            }

            Restriction restriction = restrictionComponent.toRestriction(elContext, component);
            restriction.setIndex(index);

            if (addToValue == null && checkDebug) {
                logger.log(Level.INFO, "Expression ''addTo'' is null. Restriction ''{0}'' of component ''{1}'' will not be added", new Object[]{restriction.getProperty(), component.getClientId()});
            }

            //try to remove values
            removeValues(restriction, restrictions, component, checkDebug);
//            removeEmptyValues(restriction, restrictions, component, checkDebug);

            //if the new value is not empty then add the restriction
            if (restrictionComponent.isRendered(elContext) == true && !isEmpty(restriction.getValue())) {
                if (restrictions != null) {
                    if (checkDebug) {
                        logger.log(Level.INFO, "Restriction added: {0}. Component: {1}", new Object[]{restriction, component.getClientId()});
                    }
                    //verify unique
                    boolean found = false;
                    for (Restriction current : restrictions) {
                        if (isSameRestriction(restriction, current)) {
                            current.setValue(restriction.getValue());
                            current.setIndex(index);
                            found = true;
                            break;
                        }
                    }
                    //add only if not found
                    if (found == false) {
                        restrictions.add(restriction);
                    }

                    //set locally
                    this.currentRestrictions = restrictions;

                }
            }

        }
    }

    public boolean isSameRestriction(Restriction restriction, Restriction other) {
        //o novo metodo compara pelo id do componente, antes era verificado apenas o nome e o tipo da restricao
        // return restriction.getProperty().equals(other.getProperty()) && restriction.getRestrictionType().equals(other.getRestrictionType());
        return restriction.getComponentId() != null && restriction.getComponentId().equals(other.getComponentId());
    }

    public void removeValues(Restriction restriction, List<Restriction> restrictions, UIComponent component, boolean checkDebug) {
        if (restrictions != null) {
            Iterator<Restriction> itRestrictions = restrictions.iterator();
            while (itRestrictions.hasNext()) {
                Restriction current = itRestrictions.next();
                if (isSameRestriction(restriction, current)) {
                    if (checkDebug) {
                        logger.log(Level.INFO, "Restriction removed: {0}. Component: {1}", new Object[]{current, component.getClientId()});
                    }
                    itRestrictions.remove();
                    break;
                }
            }
        }
    }

    public void removeEmptyValues(Restriction restriction, List<Restriction> restrictions, UIComponent component, boolean checkDebug) {
        //if new value is empty then remove restriction
        if (isEmpty(restriction.getValue()) && restrictions != null) {
            //use iterator to remove
            Iterator<Restriction> itRestrictions = restrictions.iterator();
            while (itRestrictions.hasNext()) {
                Restriction current = itRestrictions.next();
                if (current.getProperty().equals(restriction.getProperty()) && current.getRestrictionType().equals(restriction.getRestrictionType())) {
                    if (checkDebug) {
                        logger.log(Level.INFO, "Restriction removed: {0}. Component: {1}", new Object[]{current, component.getClientId()});
                    }
                    itRestrictions.remove();
                    break;
                }
            }
        }
    }

    public static boolean isEmpty(Object value) {

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
        Object[] state = new Object[2];
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
