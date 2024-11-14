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
    boolean isActive();

    /**
     * Sets if this User is active
     *
     * @param active
     */
    void setActive(boolean active);

    /**
     * Returns the user password
     *
     * @return user password
     */
    String getUserPassword();

    /**
     * Sets the user password
     *
     * @param password
     */
    void setUserPassword(String password);

    /**
     * Returns the user login
     *
     * @return
     */
    String getUserLogin();

    /**
     * Sets the user login
     *
     * @param login user login
     */
    void setUserLogin(String login);
}
