package com.xpert.faces.component.initializer;

import jakarta.faces.component.UIComponentBase;
import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class Initializer extends UIComponentBase implements Serializable {

    private static final long serialVersionUID = 2882707808520912818L;
    
    private static final String COMPONENT_FAMILY = "com.xpert.component";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
}
