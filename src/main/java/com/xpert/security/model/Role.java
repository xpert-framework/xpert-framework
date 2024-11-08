package com.xpert.security.model;

/**
 * Define a security role
 *
 * @author ayslan
 */
public interface Role {

    /**
     * Returns a unique identify of the Role
     *
     * @return unique identify of the Role
     */
    String getKey();

    /**
     * Sets a unique identify of the Role
     *
     * @param key role key name
     */
    void setKey(String key);

    /**
     * Returns the URL of Role. Multiple urls can be used, separated with comma
     *
     * @return
     */
    String getUrl();

    /**
     * Sets the URL of Role. Multiple urls can be used, separated with comma
     *
     * @param url The url name
     */
    void setUrl(String url);
}
