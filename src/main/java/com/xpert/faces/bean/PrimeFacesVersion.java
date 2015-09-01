package com.xpert.faces.bean;

import com.xpert.faces.primefaces.PrimeFacesUtils;

/**
 *
 * @author ayslan
 */
public class PrimeFacesVersion {

    public boolean isPrimeFaces3() {
        return PrimeFacesUtils.isVersion3();
    }
    
}
