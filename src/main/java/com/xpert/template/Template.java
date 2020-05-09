package com.xpert.template;

import com.xpert.faces.utils.FacesUtils;

/**
 *
 * @author ayslanms
 */
public class Template {

    public static Icons icons() {
        return FacesUtils.evaluateExpressionGet("#{icons}");

    }

}
