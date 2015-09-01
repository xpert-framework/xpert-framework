package com.xpert.security.model;

/**
 *
 * @author ayslan
 */
public interface User {

    public boolean isActive();

    public void setActive(boolean active);

    public String getUserPassword();

    public void setUserPassword(String password);

    public String getUserLogin();

    public void setUserLogin(String login);
}
