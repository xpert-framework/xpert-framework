package com.xpert.faces.bean;

import com.xpert.i18n.XpertResourceBundle;
import java.util.ArrayList;
import jakarta.faces.model.SelectItem;

/**
 * ManagedBean to store a jakarta.faces.model.SelectItem list of boolean
 * values and a empty value. This managed-bean can be used in EL #{booleanSelectItensEmptyOption}.
 * 
 * 
 * @author ayslan
 */
public class BooleanSelectItensEmptyOption extends ArrayList<SelectItem> {

    public BooleanSelectItensEmptyOption() {
        add(new SelectItem("", ""));
        add(new SelectItem("true", XpertResourceBundle.get("yes")));
        add(new SelectItem("false", XpertResourceBundle.get("no")));
    }
    
}
