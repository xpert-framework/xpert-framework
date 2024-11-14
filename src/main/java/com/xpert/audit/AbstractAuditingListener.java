package com.xpert.audit;

import com.xpert.audit.model.AbstractAuditing;

/**
 * Listener called on audit event
 * 
 * @author ayslan
 */
public interface AbstractAuditingListener {
    
    void onSave(AbstractAuditing abstractAuditing);
    
}
