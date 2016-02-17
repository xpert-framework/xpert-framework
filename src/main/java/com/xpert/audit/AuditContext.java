package com.xpert.audit;

import com.xpert.audit.model.AbstractAuditing;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;

/**
 * Represents que current context of audit, the "audits" form a object must be
 * added to a map, with this it can be retrieve in other moment.
 *
 * The values are put in "facesContext.getExternalContext().getRequestMap()"
 *
 * @author ayslan
 */
public class AuditContext {

    private static final String INSTANCE_KEY = AuditContext.class.getName();

    private boolean active = false;
    private final Map<Object, AbstractAuditing> auditValues = new HashMap<Object, AbstractAuditing>();

    private AuditContext() {

    }

    /**
     * Current instance associated with the request. The value is stored in
     * "facesContext.getExternalContext().getRequestMap()"
     *
     * @return A AuditContext instance, returns null if
     * FacesContext.getCurrentInstance() is null
     */
    public static AuditContext getCurrentInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            AuditContext auditContext = (AuditContext) facesContext.getExternalContext().getRequestMap().get(INSTANCE_KEY);
            if (auditContext == null) {
                auditContext = new AuditContext();
                facesContext.getExternalContext().getRequestMap().put(INSTANCE_KEY, auditContext);
            }
            return auditContext;
        }
        return null;
    }

    /**
     * @param object
     * @return The current audit of object
     */
    public AbstractAuditing getAuditing(Object object) {
        return auditValues.get(object);
    }

    /**
     * @param object
     * @return The current metadatas of object
     */
    public List getMetadata(Object object) {
        AbstractAuditing auditing = getAuditing(object);
        if (auditing != null) {
            return auditing.getMetadatas();
        }
        return null;
    }

    /**
     * Set the current audit in object
     *
     * @param object
     * @param auditing
     */
    public void setAuditing(Object object, AbstractAuditing auditing) {
        auditValues.put(object, auditing);
    }

    /**
     * Return if AuditConext is active. Default value is "false"
     *
     * @return true if AuditConext is active. Default value is "false"
     */
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
