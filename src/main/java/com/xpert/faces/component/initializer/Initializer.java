package com.xpert.faces.component.initializer;

import jakarta.faces.component.UIComponentBase;

/**
 *
 * @author ayslan
 */
public class Initializer extends UIComponentBase {

    private static final String COMPONENT_FAMILY = "com.xpert.component";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
}
