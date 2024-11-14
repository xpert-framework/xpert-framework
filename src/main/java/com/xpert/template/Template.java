package com.xpert.template;

import com.xpert.faces.utils.FacesUtils;
import java.io.Serializable;

/**
 *
 * @author ayslanms
 */
public class Template implements Serializable {

    private static final long serialVersionUID = -1242023330202904167L;

    public static Icons icons() {
        return FacesUtils.evaluateExpressionGet("#{icons}");
    }

}
