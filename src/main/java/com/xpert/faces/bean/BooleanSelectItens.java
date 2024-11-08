package com.xpert.faces.bean;

import com.xpert.i18n.XpertResourceBundle;
import jakarta.enterprise.context.RequestScoped;
import java.util.ArrayList;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * ManagedBean to store a jakarta.faces.model.SelectItem list of boolean values.
 * This managed-bean can be used in EL #{booleanSelectItens}
 * 
 * @author ayslan
 */
@Named("booleanSelectItens")
@RequestScoped
public class BooleanSelectItens extends ArrayList<SelectItem> implements Serializable {

    private static final long serialVersionUID = 5425944014365238681L;

    public BooleanSelectItens() {
        add(new SelectItem(true, XpertResourceBundle.get("yes")));
        add(new SelectItem(false, XpertResourceBundle.get("no")));
    }
    
}
