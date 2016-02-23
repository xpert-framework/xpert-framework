package com.xpert.audit;

import com.xpert.audit.model.AbstractAuditing;

/**
 * Listener called on audit event
 * 
 * @author ayslan
 */
public interface AbstractAuditingListener {
    
    public void onSave(AbstractAuditing abstractAuditing);
    
}
