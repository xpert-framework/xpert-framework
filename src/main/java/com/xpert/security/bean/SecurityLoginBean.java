package com.xpert.security.bean;

import com.xpert.core.exception.BusinessException;
import com.xpert.faces.utils.FacesMessageUtils;
import com.xpert.faces.utils.FacesUtils;
import com.xpert.security.EncryptionType;
import com.xpert.security.model.User;
import com.xpert.security.session.AbstractUserSession;
import com.xpert.utils.Encryption;
import java.security.NoSuchAlgorithmException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

/**
 * A generic bean to do the login in security control
 *
 * @author ayslan
 */
public abstract class SecurityLoginBean {

    private String userLogin;
    private String userPassword;

    protected EncryptionType getEncryptionType() {
        return EncryptionType.SHA256;
    }

    /**
     * Return if the login is "ignore case". Example
     *
     * "admin" == "ADMIN" and "admin" == "Admin"
     *
     * @return
     */
    protected boolean isLoginIgnoreCase() {
        return false;
    }

    /**
     * Return if the login is "upper case"
     *
     * @return
     */
    protected boolean isLoginUpperCase() {
        return true;
    }

    /**
     * Return if the login is "lower case"
     *
     * @return
     */
    protected boolean isLoginLowerCase() {
        return false;
    }

    /**
     * Indicate if a validation must be done if no roles are found
     *
     * @return
     */
    protected boolean isValidateWhenNoRolesFound() {
        return true;
    }

    /**
     * Return the "user" class
     *
     * @return
     */
    protected Class getUserClass() {
        return null;
    }

    /**
     * Return the user session, after a sucessful login the user is stored in
     * this bean
     *
     * @return
     */
    protected abstract AbstractUserSession getUserSession();

    protected abstract EntityManager getEntityManager();

    /**
     * Define something when login is successful like add a message "Welcome
     * user"
     *
     * @param user
     */
    protected void onSucess(User user) {
    }

    /**
     * Define something when login is unsuccessful
     *
     */
    protected void onError() {
    }

    /**
     * Redirect to this page when login is successful
     *
     * @return
     */
    protected abstract String getRedirectPageWhenSucess();

    /**
     * Redirect to this page when user logout
     *
     * @return
     */
    protected abstract String getRedirectPageWhenLogout();

    /**
     * Message when user was not found.
     *
     * @return
     */
    protected String getUserNotFoundMessage() {
        return "User not found";
    }

    protected String getUserWithoutPassword() {
        return "User with no password in database";
    }

    /**
     * Message when user is not active not found.
     *
     * @return
     */
    protected String getInactiveUserMessage() {
        return "Inactive User";
    }

    /**
     * Message when there is no role.
     *
     * @return
     */
    protected String getNoRolesFoundMessage() {
        return "No roles found for this user";
    }

    /**
     * Executed before user query in database
     *
     * @return
     */
    protected boolean validate() {
        boolean valid = true;
        if (userLogin == null || userLogin.trim().isEmpty()) {
            addErrorMessage("User is required");
            valid = false;
        }
        if (userPassword == null || userPassword.trim().isEmpty()) {
            addErrorMessage("Password is required");
            valid = false;
        }
        return valid;
    }

    /**
     * Executed after user query in database
     *
     * @param user
     * @return
     * @throws com.xpert.core.exception.BusinessException
     */
    protected boolean validate(User user) throws BusinessException {
        return true;
    }

    protected void logout() {
        FacesUtils.invalidateSession();
        FacesUtils.redirect(getRedirectPageWhenLogout());
    }

    /**
     *
     * @return Query String to find User
     */
    protected String getUserLoginQueryString() {
        String queryString = " FROM " + getUserClass().getName();
        if (isLoginIgnoreCase()) {
            queryString = queryString + " WHERE UPPER(userLogin) = UPPER(:userLogin) ";
        } else {
            queryString = queryString + " WHERE userLogin = :userLogin ";
        }
        return queryString;
    }

    /**
     *
     * @param entityManager
     * @param queryString String to find User
     * @param login User Login
     * @return
     */
    protected Query getUserLoginQuery(EntityManager entityManager, String queryString, String login) {
        return entityManager.createQuery(queryString).setParameter("userLogin", login);
    }

    protected User getUser(String login, String password) {

        User user = null;
        EntityManager entityManager = getEntityManager();
        if (entityManager == null || getUserClass() == null) {
            throw new IllegalArgumentException("To get the user you must override methods getEntityManager() and getUserClass() or override getUser() and do your own logic");
        }

        String queryString = getUserLoginQueryString();

        if (isLoginUpperCase()) {
            login = login.toUpperCase();
        } else if (isLoginLowerCase()) {
            login = login.toLowerCase();
        }
        try {
            user = (User) getUserLoginQuery(entityManager, queryString, login).getSingleResult();
        } catch (NoResultException ex) {
            //
        }

        //verify user password
        if (user != null) {
            user = authenticateUserPassword(user, password);
        }

        return user;
    }

    /**
     * Authenticates a user withe the password. If authentication fals then
     * return null.
     *
     * @param user
     * @param password
     * @return
     */
    protected User authenticateUserPassword(User user, String password) {
        //compare password encryptedPassword
        if (user != null && user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            try {
                String encryptedPassword = null;
                if (getEncryptionType() != null) {
                    if (getEncryptionType().equals(EncryptionType.SHA256)) {
                        encryptedPassword = Encryption.getSHA256(password);
                    } else if (getEncryptionType().equals(EncryptionType.SHA512)) {
                        encryptedPassword = Encryption.getSHA512(password);
                    } else if (getEncryptionType().equals(EncryptionType.MD5)) {
                        encryptedPassword = Encryption.getMD5(password);
                    }
                } else {
                    encryptedPassword = password;
                }

                if (!user.getUserPassword().equals(encryptedPassword)) {
                    return null;
                }
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }

        }
        return user;
    }

    /**
     * This method make the user login
     */
    public void login() {

        //clear user session
        if (getUserSession() != null) {
            getUserSession().setUser(null);
        }

        if (validate()) {
            User user = getUser(userLogin, userPassword);

            if (user == null) {
                addErrorMessage(getUserNotFoundMessage());
                onError();
                return;
            }
            if (!user.isActive()) {
                addErrorMessage(getInactiveUserMessage());
                onError();
                return;
            }
            if (user.getUserPassword() == null || user.getUserPassword().isEmpty()) {
                addErrorMessage(getUserWithoutPassword());
                onError();
                return;
            }
            try {
                //more validation
                validate(user);
            } catch (BusinessException ex) {
                FacesMessageUtils.error(ex);
                return;
            }

            //set user in session
            if (getUserSession() != null) {
                getUserSession().setUser(user);
                getUserSession().createSession();
                //when no roles found, login is not sucessful
                if (isValidateWhenNoRolesFound() && (getUserSession().getRoles() == null || getUserSession().getRoles().isEmpty())) {
                    addErrorMessage(getNoRolesFoundMessage());
                    getUserSession().setUser(null);
                    return;
                }
            }
            onSucess(user);
            FacesUtils.redirect(getRedirectPageWhenSucess());
        }

    }

    /**
     * Add a FacesMessage.SEVERITY_ERROR in context
     *
     * @param message
     */
    protected void addErrorMessage(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    /**
     * Returns the user login
     *
     * @return
     */
    public String getUserLogin() {
        return userLogin;
    }

    /**
     * Sets the user login
     *
     * @param userLogin
     */
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    /**
     * Returns the user password
     *
     * @return
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * Sets the user login
     *
     * @param userPassword
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
