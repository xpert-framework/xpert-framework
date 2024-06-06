package com.xpert.faces.component.security;

import com.xpert.security.SecuritySessionManager;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.context.FacesContext;

/**
 *
 * @author ayslan
 */
public class SecurityArea extends UIComponentBase {

    private static final String COMPONENT_FAMILY = "com.xpert.component";

    protected enum PropertyKeys {

        rolesAllowed;
        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }
        if (hasRole(context)) {
            super.processDecodes(context);
        }
    }
    
    public boolean hasRole(FacesContext context){
        return SecuritySessionManager.hasRole(getRolesAllowed(), context);
    }

    public String getRolesAllowed() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.rolesAllowed);
    }

    public void setRolesAllowed(String _roles) {
        getStateHelper().put(PropertyKeys.rolesAllowed, _roles);
    }
}
