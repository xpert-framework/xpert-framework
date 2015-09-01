package com.xpert.faces.bootstrap;

/**
 * Represents the bootstrap version.
 * Bootstrap link: http://getbootstrap.com/
 *  
 * @author ayslan
 */
public enum BootstrapVersion {

    VERSION_3("Version 3", "col-lg-4 col-md-6 col-sm-6 col-xs-12");

    private final String description;
    private final String defaultColumns;

    private BootstrapVersion(String description, String defaultColumns) {
        this.description = description;
        this.defaultColumns = defaultColumns;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultColumns() {
        return defaultColumns;
    }
    

}
