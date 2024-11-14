package com.xpert.security.filter;

import com.xpert.faces.utils.FacesUtils;
import com.xpert.security.SecuritySessionManager;
import com.xpert.security.session.AbstractUserSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * A generic filter to security control
 *
 * @author ayslan
 */
public abstract class AbstractSecurityFilter implements Filter, Serializable {

    private static final long serialVersionUID = -2429458515040152546L;
    
    private static final Logger logger = Logger.getLogger(AbstractSecurityFilter.class.getName());

    /**
     * Name of session bean to get from HttpSession
     *
     * @return
     */
    public abstract String getUserSessionName();

    /**
     * Custom autentication. Define here more authetication logic
     *
     * @param request
     * @param response
     * @return
     */
    public boolean getMoreAuthentication(ServletRequest request, ServletResponse response) {
        return true;
    }

    /**
     * Page to redirect if autentication fails
     *
     * @return
     */
    public abstract String getHomePage();

    /**
     * Define a logic to error. Called on exception in method
     * "chain.doFilter(request, response);"
     */
    public void onError() {
    }

    /**
     * URLs the are ignored on requests after user authenticated
     *
     * @return
     */
    public abstract String[] getIgnoredUrls();

    /**
     * Return a AbstractUserSession. This method can be override a custom way to
     * obtain a AbstractUserSession instance.
     *
     * @param request a ServletRequest from filter method "doFilter
     * @return AbstractUserSession from session usually a Session Scoped
     */
    public AbstractUserSession getSessionBean(ServletRequest request) {
        return (AbstractUserSession) getFromSession(request, getUserSessionName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        AbstractUserSession userSession = getSessionBean(request);

        if (userSession == null || !isAuthenticated(userSession)) {
            if (isDebug()) {
                logger.log(Level.INFO, "User not authenticated redirecting to: {0}", getHomePage());
            }
            redirectHome(request, response);
            return;
        }

        if (!hasUrl((HttpServletRequest) request)) {
            if (isDebug()) {
                logger.log(Level.INFO, "User {0} not authorized to url: {1}", new Object[]{userSession.getUser().getUserLogin(), ((HttpServletRequest) request).getRequestURI()});
            }
            redirectHome(request, response);
            return;
        }

        if (getMoreAuthentication(request, response)) {
            try {
                chain.doFilter(request, response);
            } catch (Throwable ex) {
                logger.log(Level.SEVERE, null, ex);
                onError();
            }
        }

    }

    /**
     * @param request
     * @param attribute Attribute name of the object in session
     * @return a Object from session
     */
    public Object getFromSession(ServletRequest request, String attribute) {
        return ((HttpServletRequest) request).getSession().getAttribute(attribute);
    }

    /**
     * Return true if the current session contains the current URL
     *
     * @param request
     * @return
     */
    public boolean hasUrl(HttpServletRequest request) {
        String currentView = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        if (getIgnoredUrls() != null && Arrays.asList(getIgnoredUrls()).contains(currentView)) {
            return true;
        }
        return SecuritySessionManager.hasURL(currentView, request);
    }

    public void redirectHome(ServletRequest request, ServletResponse response) {
        //create faces context
        FacesUtils.getFacesContext((HttpServletRequest) request, (HttpServletResponse) response);
        FacesUtils.redirect(getHomePage());
    }

    /**
     * Log events on filter
     *
     * @return
     */
    public boolean isDebug() {
        return true;
    }

    /**
     * Return true if userSession has a user authenticated calling method
     * AbstractUserSession.isAuthenticated
     *
     * @param userSession
     * @return
     */
    public boolean isAuthenticated(AbstractUserSession userSession) {
        return userSession.isAuthenticated();
    }
}
