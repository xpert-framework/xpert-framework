package com.xpert.faces.primefaces;

/**
 * Enum to represent Primefaces version
 *  
 * @author ayslan
 */
public enum PrimeFacesVersion {
    
    VERSION_3("Version 3", false, false), 
    VERSION_4("Version 4", true, true),
    VERSION_5("Version 5 (or greater)", true, true);
    
    private final String description;
    /**
     * new version uses widget var is like "PF('widgetVar')"
     */
    private final boolean usePFWidgetVar;
    /**
     * version 3 use appendToBody, greater version use appenTo="@(body)"
     */
    private final boolean useAppendTo;

    private PrimeFacesVersion(String description, boolean usePFWidgetVar, boolean useAppendTo) {
        this.description = description;
        this.usePFWidgetVar = usePFWidgetVar;
        this.useAppendTo = useAppendTo;
    }

    public boolean isUsePFWidgetVar() {
        return usePFWidgetVar;
    }

    public boolean isUseAppendTo() {
        return useAppendTo;
    }
    
    public String getDescription() {
        return description;
    }

    
}
