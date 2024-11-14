package com.xpert.security.session;

import com.xpert.security.SecuritySessionManager;
import com.xpert.security.model.Role;
import com.xpert.security.model.User;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ayslan
 */
public abstract class AbstractUserSession implements Serializable {

    private static final long serialVersionUID = 1551367246829752466L;

    public void createSession() {
        SecuritySessionManager.clearRoles();
        SecuritySessionManager.putRoles(getRoles());
    }

    public abstract User getUser();

    public abstract void setUser(User user);

    public abstract List<Role> getRoles();

    public boolean hasRole(String role) {
        return SecuritySessionManager.hasRole(role);
    }
    public boolean isAuthenticated() {
        return getUser() != null;
    }
}
