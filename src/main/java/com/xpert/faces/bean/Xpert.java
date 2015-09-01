package com.xpert.faces.bean;

import com.xpert.faces.primefaces.PrimeFacesUtils;

/**
 * Generic managed bean to acess utils methods and funtions from xpert-framework
 *
 * @author Ayslan
 */
public class Xpert {

    public String normalizePrimeFacesWidget(String widgetVar) {
        return PrimeFacesUtils.normalizeWidgetVar(widgetVar);
    }

}
