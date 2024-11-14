/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xpert.faces.component.api;

import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.NamingContainer;
import jakarta.faces.component.UIColumn;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.UniqueIdVendor;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitHint;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PostValidateEvent;
import jakarta.faces.event.PreValidateEvent;
import jakarta.faces.model.ArrayDataModel;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.model.ResultSetDataModel;
import jakarta.faces.model.ScalarDataModel;
import jakarta.faces.render.Renderer;
import java.io.Serializable;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * UIData from primfaces
 */
public class UIData extends jakarta.faces.component.UIData implements Serializable {

    private static final long serialVersionUID = 2568322413182865531L;

    private String clientId = null;
    private StringBuilder idBuilder = new StringBuilder();
    private DataModel model = null;
    private Object oldVar = null;

    protected enum PropertyKeys {

        saved, rowIndex, rowIndexVar;

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
    
    public static UniqueIdVendor findParentUniqueIdVendor(UIComponent component) {
        UIComponent parent = component.getParent();

        while(parent != null) {
            if(parent instanceof UniqueIdVendor) {
                return (UniqueIdVendor) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }
    
     public static UIComponent findParentNamingContainer(UIComponent component) {
        UIComponent parent = component.getParent();

        while(parent != null) {
            if(parent instanceof NamingContainer) {
                return (UIComponent) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public java.lang.String getRowIndexVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rowIndexVar, null);
    }

    public void setRowIndexVar(java.lang.String _rowIndexVar) {
        getStateHelper().put(PropertyKeys.rowIndexVar, _rowIndexVar);
    }


    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        processPhase(context, PhaseId.APPLY_REQUEST_VALUES);
        decode(context);
        popComponentFromEL(context);
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        Application app = context.getApplication();
        app.publishEvent(context, PreValidateEvent.class, this);
        processPhase(context, PhaseId.PROCESS_VALIDATIONS);
        app.publishEvent(context, PostValidateEvent.class, this);
        popComponentFromEL(context);
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        processPhase(context, PhaseId.UPDATE_MODEL_VALUES);
        popComponentFromEL(context);
    }

    protected void processPhase(FacesContext context, PhaseId phaseId) {
        if (shouldSkipChildren(context)) {
            return;
        }

        setRowIndex(-1);
        processFacets(context, phaseId);
        if (requiresColumns()) {
            processColumnFacets(context, phaseId);
        }
        processChildren(context, phaseId);
        setRowIndex(-1);
    }

    protected void processFacets(FacesContext context, PhaseId phaseId) {
        if (this.getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                process(context, facet, phaseId);
            }
        }
    }

    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
        for (UIComponent child : this.getChildren()) {
            if (child.isRendered() && (child.getFacetCount() > 0)) {
                for (UIComponent facet : child.getFacets().values()) {
                    process(context, facet, phaseId);
                }
            }
        }
    }

    protected void processChildren(FacesContext context, PhaseId phaseId) {
        int first = getFirst();
        int rows = getRows();
        int last = rows == 0 ? getRowCount() : (first + rows);

        for (int rowIndex = first; rowIndex < last; rowIndex++) {
            setRowIndex(rowIndex);

            if (!isRowAvailable()) {
                break;
            }

            for (UIComponent child : this.getIterableChildren()) {
                if (child.isRendered()) {
                    if (child instanceof Column) {
                        for (UIComponent grandkid : child.getChildren()) {
                            process(context, grandkid, phaseId);
                        }
                    } else {
                        process(context, child, phaseId);
                    }
                }
            }
        }
    }

    protected void process(FacesContext context, UIComponent component, PhaseId phaseId) {
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            component.processDecodes(context);
        } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            component.processValidators(context);
        } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            component.processUpdates(context);
        }

    }

    @Override
    public String getClientId(FacesContext context) {
        if (this.clientId != null) {
            return this.clientId;
        }

        String id = getId();
        if (id == null) {
            UniqueIdVendor parentUniqueIdVendor = findParentUniqueIdVendor(this);

            if (parentUniqueIdVendor == null) {
                UIViewRoot viewRoot = context.getViewRoot();

                if (viewRoot != null) {
                    id = viewRoot.createUniqueId();
                } else {
                    throw new FacesException("Cannot create clientId for " + this.getClass().getCanonicalName());
                }
            } else {
                id = parentUniqueIdVendor.createUniqueId(context, null);
            }

            this.setId(id);
        }

        UIComponent namingContainer = findParentNamingContainer(this);
        if (namingContainer != null) {
            String containerClientId = namingContainer.getContainerClientId(context);

            if (containerClientId != null) {
                this.clientId = this.idBuilder.append(containerClientId).append(UINamingContainer.getSeparatorChar(context)).append(id).toString();
                this.idBuilder.setLength(0);
            } else {
                this.clientId = id;
            }
        } else {
            this.clientId = id;
        }

        Renderer renderer = getRenderer(context);
        if (renderer != null) {
            this.clientId = renderer.convertClientId(context, this.clientId);
        }

        return this.clientId;
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        //clientId is without rowIndex
        String componentClientId = this.getClientId(context);

        int rowIndex = getRowIndex();
        if (rowIndex == -1) {
            return componentClientId;
        }

        String containerClientId = idBuilder.append(componentClientId).append(UINamingContainer.getSeparatorChar(context)).append(rowIndex).toString();
        idBuilder.setLength(0);

        return containerClientId;
    }

    @Override
    public void setId(String id) {
        super.setId(id);

        //clear
        this.clientId = null;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        saveDescendantState();

        setRowModel(rowIndex);

        restoreDescendantState();
    }

    public void setRowModel(int rowIndex) {
        //update rowIndex
        getStateHelper().put(PropertyKeys.rowIndex, rowIndex);
        getDataModel().setRowIndex(rowIndex);

        //clear datamodel
        if (rowIndex == -1) {
            setDataModel(null);
        }

        //update var
        String var = (String) this.getVar();
        if (var != null) {
            String rowIndexVar = this.getRowIndexVar();
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();

            if (rowIndex == -1) {
                oldVar = requestMap.remove(var);

                if (rowIndexVar != null) {
                    requestMap.remove(rowIndexVar);
                }
            } else if (isRowAvailable()) {
                requestMap.put(var, getRowData());

                if (rowIndexVar != null) {
                    requestMap.put(rowIndexVar, rowIndex);
                }
            } else {
                requestMap.remove(var);

                if (rowIndexVar != null) {
                    requestMap.put(rowIndexVar, rowIndex);
                }

                if (oldVar != null) {
                    requestMap.put(var, oldVar);
                    oldVar = null;
                }
            }
        }
    }

    @Override
    public int getRowIndex() {
        return (Integer) getStateHelper().eval(PropertyKeys.rowIndex, -1);
    }

    protected void saveDescendantState() {
        FacesContext context = getFacesContext();

        if (this.getChildCount() > 0) {
            for (UIComponent kid : getChildren()) {
                saveDescendantState(kid, context);
            }
        }

        if (this.getFacetCount() > 0) {
            for (UIComponent facet : this.getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }
    }

    protected void saveDescendantState(UIComponent component, FacesContext context) {
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            SavedState state = null;
            String componentClientId = component.getClientId(context);

            if (saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, componentClientId, state);
            }

            if (state == null) {
                state = saved.get(componentClientId);

                if (state == null) {
                    state = new SavedState();
                    getStateHelper().put(PropertyKeys.saved, componentClientId, state);
                }
            }

            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        } else if (component instanceof UIForm) {
            UIForm form = (UIForm) component;
            String componentClientId = component.getClientId(context);
            SavedState state = null;
            if (saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, componentClientId, state);
            }

            if (state == null) {
                state = saved.get(componentClientId);
                if (state == null) {
                    state = new SavedState();
                    //saved.put(clientId, state);
                    getStateHelper().put(PropertyKeys.saved, componentClientId, state);
                }
            }
            state.setSubmitted(form.isSubmitted());
        }

        //save state for children
        if (component.getChildCount() > 0) {
            for (UIComponent kid : component.getChildren()) {
                saveDescendantState(kid, context);
            }
        }

        //save state for facets
        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }

    }

    protected void restoreDescendantState() {
        FacesContext context = getFacesContext();

        if (getChildCount() > 0) {
            for (UIComponent kid : getChildren()) {
                restoreDescendantState(kid, context);
            }
        }

        if (this.getFacetCount() > 0) {
            for (UIComponent facet : this.getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }
    }

    protected void restoreDescendantState(UIComponent component, FacesContext context) {
        String id = component.getId();
        component.setId(id); //reset the client id
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String componentClientId = component.getClientId(context);

            SavedState state = saved.get(componentClientId);
            if (state == null) {
                state = new SavedState();
            }

            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        } else if (component instanceof UIForm) {
            UIForm form = (UIForm) component;
            String componentClientId = component.getClientId(context);
            SavedState state = saved.get(componentClientId);
            if (state == null) {
                state = new SavedState();
            }

            form.setSubmitted(state.getSubmitted());
            state.setSubmitted(form.isSubmitted());
        }

        //restore state of children
        if (component.getChildCount() > 0) {
            for (UIComponent kid : component.getChildren()) {
                restoreDescendantState(kid, context);
            }
        }

        //restore state of facets
        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }

    }

    @Override
    protected DataModel getDataModel() {
        if (this.model != null) {
            return (model);
        }

        Object current = getValue();
        if (current == null) {
            setDataModel(new ListDataModel(Collections.EMPTY_LIST));
        } else if (current instanceof DataModel) {
            setDataModel((DataModel) current);
        } else if (current instanceof List) {
            setDataModel(new ListDataModel((List) current));
        } else if (Object[].class.isAssignableFrom(current.getClass())) {
            setDataModel(new ArrayDataModel((Object[]) current));
        } else if (current instanceof ResultSet) {
            setDataModel(new ResultSetDataModel((ResultSet) current));
        }else {
            setDataModel(new ScalarDataModel(current));
        }

        return model;
    }

    @Override
    protected void setDataModel(DataModel dataModel) {
        this.model = dataModel;
    }

    protected boolean shouldSkipChildren(FacesContext context) {
        return false;
    }

    protected boolean shouldVisitChildren(VisitContext context, boolean visitRows) {
        if (visitRows) {
            setRowIndex(-1);
        }

        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);

        return (!idsToVisit.isEmpty());
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        if (!isVisitable(context)) {
            return false;
        }

        FacesContext facesContext = context.getFacesContext();
        boolean visitRows = shouldVisitRows(facesContext, context);

        int rowIndex = -1;
        if (visitRows) {
            rowIndex = getRowIndex();
            setRowIndex(-1);
        }

        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if (result == VisitResult.COMPLETE) {
                return true;
            }

            if ((result == VisitResult.ACCEPT) && shouldVisitChildren(context, visitRows)) {
                if (visitFacets(context, callback, visitRows)) {
                    return true;
                }

                if (requiresColumns() && visitColumnsAndColumnFacets(context, callback, visitRows)) {
                    return true;
                }

                if (visitRows(context, callback, visitRows)) {
                    return true;
                }

            }
        } finally {
            popComponentFromEL(facesContext);

            if (visitRows) {
                setRowIndex(rowIndex);
            }
        }

        return false;
    }

    protected boolean visitFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (visitRows) {
            setRowIndex(-1);
        }

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (facet.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean visitColumnsAndColumnFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (visitRows) {
            setRowIndex(-1);
        }

        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                VisitResult result = context.invokeVisitCallback(child, callback); // visit the column directly
                if (result == VisitResult.COMPLETE) {
                    return true;
                }

                if (child instanceof UIColumn) {
                    if (child.getFacetCount() > 0) {
                        if (child instanceof Columns) {
                            Columns columns = (Columns) child;
                            for (int i = 0; i < columns.getRowCount(); i++) {
                                columns.setRowIndex(i);
                                boolean value = visitColumnFacets(context, callback, child);
                                if (value) {
                                    return true;
                                }
                            }
                            columns.setRowIndex(-1);
                        } else {
                            boolean value = visitColumnFacets(context, callback, child);
                            if (value) {
                                return true;
                            }
                        }

                    }
                } else if (child instanceof ColumnGroup) {
                    visitColumnGroup(context, callback, (ColumnGroup) child);
                }
            }
        }

        return false;
    }

    protected boolean visitColumnGroup(VisitContext context, VisitCallback callback, ColumnGroup group) {
        if (group.getChildCount() > 0) {
            for (UIComponent row : group.getChildren()) {
                if (row.getChildCount() > 0) {
                    for (UIComponent col : row.getChildren()) {
                        if (col instanceof Column && col.getFacetCount() > 0) {
                            boolean value = visitColumnFacets(context, callback, (Column) col);
                            if (value) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean visitColumnFacets(VisitContext context, VisitCallback callback, UIComponent component) {
        for (UIComponent columnFacet : component.getFacets().values()) {
            if (columnFacet.visitTree(context, callback)) {
                return true;
            }
        }

        return false;
    }

    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        boolean requiresColumns = this.requiresColumns();
        int processed = 0;
        int rowIndex = 0;
        int rows = 0;
        if (visitRows) {
            rowIndex = getFirst() - 1;
            rows = getRows();
        }

        while (true) {
            if (visitRows) {
                if ((rows > 0) && (++processed > rows)) {
                    break;
                }

                setRowIndex(++rowIndex);
                if (!isRowAvailable()) {
                    break;
                }
            }

            if (getChildCount() > 0) {
                for (UIComponent kid : getChildren()) {

                    if (requiresColumns) {
                        if (kid instanceof Columns) {
                            Columns uicolumns = (Columns) kid;
                            for (int i = 0; i < uicolumns.getRowCount(); i++) {
                                uicolumns.setRowIndex(i);

                                boolean value = visitColumnContent(context, callback, uicolumns);
                                if (value) {
                                    uicolumns.setRowIndex(-1);
                                    return true;
                                }
                            }

                            uicolumns.setRowIndex(-1);
                        } else {
                            boolean value = visitColumnContent(context, callback, kid);
                            if (value) {
                                return true;
                            }
                        }
                    } else {
                        if (kid.visitTree(context, callback)) {
                            return true;
                        }
                    }
                }
            }

            if (!visitRows) {
                break;
            }

        }

        return false;
    }

    protected boolean visitColumnContent(VisitContext context, VisitCallback callback, UIComponent component) {
        if (component.getChildCount() > 0) {
            for (UIComponent grandkid : component.getChildren()) {
                if (grandkid.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean shouldVisitRows(FacesContext context, VisitContext visitContext) {
        try {
            //JSF 2.1
            VisitHint skipHint = VisitHint.valueOf("SKIP_ITERATION");
            return !visitContext.getHints().contains(skipHint);
        } catch (IllegalArgumentException e) {
            //JSF 2.0
            Object skipHint = context.getAttributes().get("jakarta.faces.visit.SKIP_ITERATION");
            return !Boolean.TRUE.equals(skipHint);
        }
    }

    protected boolean requiresColumns() {
        return false;
    }

    protected List<UIComponent> getIterableChildren() {
        return this.getChildren();
    }
}
