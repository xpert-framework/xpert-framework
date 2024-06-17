package com.xpert.faces.primefaces;

/**
 * Enum to represent Primefaces version
 *  
 * @author ayslan
 */
public enum PrimeFacesVersion {
    
    VERSION_14("Version 14", true, true);
    
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
