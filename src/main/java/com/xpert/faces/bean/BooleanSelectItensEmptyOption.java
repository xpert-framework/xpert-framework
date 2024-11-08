package com.xpert.faces.bean;

import com.xpert.i18n.XpertResourceBundle;
import jakarta.enterprise.context.RequestScoped;
import java.util.ArrayList;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * ManagedBean to store a jakarta.faces.model.SelectItem list of boolean values
 * and a empty value. This managed-bean can be used in EL
 * #{booleanSelectItensEmptyOption}.
 *
 *
 * @author ayslan
 */
@Named("booleanSelectItensEmptyOption")
@RequestScoped
public class BooleanSelectItensEmptyOption extends ArrayList<SelectItem> implements Serializable {

    private static final long serialVersionUID = 5381590094582654709L;

    public BooleanSelectItensEmptyOption() {
        add(new SelectItem("", ""));
        add(new SelectItem("true", XpertResourceBundle.get("yes")));
        add(new SelectItem("false", XpertResourceBundle.get("no")));
    }

}
