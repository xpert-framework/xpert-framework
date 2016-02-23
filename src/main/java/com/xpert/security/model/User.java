package com.xpert.security.model;

/**
 * Define a security User
 *
 * @author ayslan
 */
public interface User {

    /**
     * Returns is this User is active
     *
     * @return true if is active
     */
    public boolean isActive();

    /**
     * Sets if this User is active
     *
     * @param active
     */
    public void setActive(boolean active);

    /**
     * Returns the user password
     *
     * @return user password
     */
    public String getUserPassword();

    /**
     * Sets the user password
     *
     * @param password
     */
    public void setUserPassword(String password);

    /**
     * Returns the user login
     *
     * @return
     */
    public String getUserLogin();

    /**
     * Sets the user login
     *
     * @param login user login
     */
    public void setUserLogin(String login);
}
