package com.xpert.security;

import com.xpert.Constants;
import com.xpert.security.model.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Class to control the user session
 *
 * @author ayslan
 */
public class SecuritySessionManager implements Serializable {

    private static final long serialVersionUID = -6950447637460816712L;

    /**
     * Clears the current roles in session
     *
     * @param request
     */
    public static void clearRoles(ServletRequest request) {
        ((HttpServletRequest) request).getSession().removeAttribute(Constants.USER_ROLES);
    }

    /**
     * Clears the current roles in session (of FacesContext)
     */
    public static void clearRoles() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(Constants.USER_ROLES);
    }

    /**
     * Same as putRoles(ServletRequest request, List<Role> roles) but uses
     * FacesContext
     *
     * @param roles
     */
    public static void putRoles(List<Role> roles) {
        putRoles((ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(), roles);
    }

    /**
     * Put roles in session Map
     *
     * @param request
     * @param roles
     */
    public static void putRoles(ServletRequest request, List<Role> roles) {

        if (roles == null || roles.isEmpty()) {
            return;
        }

        /**
         * Put keys and urls in a Map to optmize perfomance
         */
        Map<String, Role> rolesMap = new HashMap<>();
        for (Role role : roles) {
            if (role.getKey() != null) {
                for (String string : role.getKey().split(",")) {
                    rolesMap.put(string, role);
                }
            }
        }
        Map<String, Role> urlsMap = new HashMap<>();
        for (Role role : roles) {
            if (role.getUrl() != null && !role.getUrl().isEmpty()) {
                for (String url : role.getUrl().split(",")) {
                    String urlFormated = null;
                    int indexOfQuery = url.indexOf("?");
                    if (indexOfQuery > -1) {
                        urlFormated = url.substring(0, indexOfQuery).trim();
                    } else {
                        urlFormated = url.trim();
                    }
                    urlsMap.put(urlFormated, role);
                }
            }
        }
        ((HttpServletRequest) request).getSession().setAttribute(Constants.USER_ROLES, roles);
        ((HttpServletRequest) request).getSession().setAttribute(Constants.USER_ROLES_KEY_MAP, rolesMap);
        ((HttpServletRequest) request).getSession().setAttribute(Constants.USER_ROLES_URL_MAP, urlsMap);

    }

    /**
     * same as hasURL(String url, ServletRequest request) but uses FacesContext
     *
     * @param url
     * @return
     */
    public static boolean hasURL(String url) {
        return hasURL(url, (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    /**
     * Verify if user can acess url.
     *
     * @param url
     * @param request
     * @return
     */
    public static boolean hasURL(String url, ServletRequest request) {
        Map<String, String> urlsMap = (Map<String, String>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES_URL_MAP);
        if (urlsMap != null && urlsMap.get(url.trim()) != null) {
            return true;
        }
        return false;
    }

    /**
     * same as hasRole(String key, ServletRequest request) but uses FacesContext
     * to get the request
     *
     * @param key
     * @param context
     * @return
     */
    public static boolean hasRole(String key, FacesContext context) {
        return hasRole(key, (ServletRequest) context.getExternalContext().getRequest());
    }

    /**
     * same as hasRole(String key, ServletRequest request) but uses FacesContext
     * to get the request
     *
     * @param key
     * @return
     */
    public static boolean hasRole(String key) {
        return hasRole(key, (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    /**
     * Verify if user has the role os a permission. It can be multiple keys
     * (separeted with commas)
     *
     * @param key
     * @param request
     * @return
     */
    public static boolean hasRole(String key, ServletRequest request) {
        if (getRole(key, request) != null) {
            return true;
        }
        return false;
    }

    /**
     * Same as getRole(String key, ServletRequest request) but uses FacesContext
     *
     * @param key
     * @return
     */
    public static Role getRole(String key) {
        return getRole(key, (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    /**
     * Get role from user. The key can be separeted with commas, so the first
     * result is retrieved
     *
     * @param key
     * @param request
     * @return
     */
    public static Role getRole(String key, ServletRequest request) {
        Map<String, Role> rolesMap = (Map<String, Role>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES_KEY_MAP);
        if (key != null && !key.trim().isEmpty() && rolesMap != null) {
            String[] keys = key.split(",");
            for (String c : keys) {
                Role role = rolesMap.get(c.trim());
                if (role != null) {
                    return role;
                }
            }
        }
        return null;
    }
    /**
     * Same as getRoleByUrl(String url, ServletRequest request) but uses FacesContext
     *
     * @param url
     * @return
     */
    public static Role getRoleByUrl(String url) {
        return getRoleByUrl(url, (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
    }

    /**
     * Get role from user by URL. The url can be separeted with commas, so the first
     * result is retrieved
     *
     * @param url
     * @param request
     * @return
     */
    public static Role getRoleByUrl(String url, ServletRequest request) {
        Map<String, Role> rolesMap = (Map<String, Role>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES_URL_MAP);
        if (url != null && !url.trim().isEmpty() && rolesMap != null) {
            String[] keys = url.split(",");
            for (String c : keys) {
                Role role = rolesMap.get(c.trim());
                if (role != null) {
                    return role;
                }
            }
        }
        return null;
    }

    /**
     * Returns the roles in current session
     *
     * @param request
     * @return
     */
    public static List<Role> getRoles(ServletRequest request) {
        return (List<Role>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @param context
     * @return
     */
    public static List<Role> getRoles(FacesContext context) {
        return (List<Role>) context.getExternalContext().getSessionMap().get(Constants.USER_ROLES);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @return
     */
    public static List<Role> getRoles() {
        return (List<Role>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.USER_ROLES);
    }

    /**
     * Returns the roles in current session.
     *
     * @param request
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesMap(ServletRequest request) {
        return (Map<String, Role>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES_KEY_MAP);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @param context
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesMap(FacesContext context) {
        return (Map<String, Role>) context.getExternalContext().getSessionMap().get(Constants.USER_ROLES_KEY_MAP);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesMap() {
        return (Map<String, Role>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.USER_ROLES_KEY_MAP);
    }
    /**
     * Returns the roles in current session.
     *
     * @param request
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesUrlMap(ServletRequest request) {
        return (Map<String, Role>) ((HttpServletRequest) request).getSession().getAttribute(Constants.USER_ROLES_URL_MAP);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @param context
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesUrlMap(FacesContext context) {
        return (Map<String, Role>) context.getExternalContext().getSessionMap().get(Constants.USER_ROLES_URL_MAP);
    }

    /**
     * Returns the roles in current session (from FacesContext)
     *
     * @return a Map (key, role)
     */
    public static Map<String, Role> getRolesUrlMap() {
        return (Map<String, Role>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(Constants.USER_ROLES_URL_MAP);
    }
}
