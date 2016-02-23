package com.xpert.faces.bean;

import com.xpert.i18n.XpertResourceBundle;
import java.util.ArrayList;
import javax.faces.model.SelectItem;

/**
 *
 * ManagedBean to store a javax.faces.model.SelectItem list of boolean values.
 * This managed-bean can be used in EL #{booleanSelectItens}
 * 
 * @author ayslan
 */
public class BooleanSelectItens extends ArrayList<SelectItem> {

    public BooleanSelectItens() {
        add(new SelectItem(true, XpertResourceBundle.get("yes")));
        add(new SelectItem(false, XpertResourceBundle.get("no")));
    }
    
}
