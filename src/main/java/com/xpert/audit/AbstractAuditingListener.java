package com.xpert.audit;

import com.xpert.audit.model.AbstractAuditing;

/**
 * Listener called on audit event
 * 
 * @author Ayslan
 */
public interface AbstractAuditingListener {
    
    public void onSave(AbstractAuditing abstractAuditing);
    
}
